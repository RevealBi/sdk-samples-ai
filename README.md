# Getting Started with Reveal AI Add-On (Private Preview)

This guide will walk you through setting up the Reveal AI Add-On in your existing Reveal SDK application.

> **⚠️ PRIVATE PREVIEW**: This is pre-release software for evaluation only. Not intended for production use. Breaking changes expected before RTM.

**Time to Complete**: 30-45 minutes

---

## Prerequisites

- ✅ **Private preview access** granted by your Reveal sales representative
- ✅ **NuGet feed credentials** (URL, username, and password will be emailed to you)
- ✅ **Reveal SDK v1.8.3+** installed and working in your ASP.NET Core app
- ✅ **.NET 8.0 SDK** installed
- ✅ **LLM Provider account** (OpenAI or Anthropic recommended)
- ✅ At least one datasource configured in Reveal SDK

---

## Step 1: Install NuGet Package

### 1a. Add Reveal AI NuGet Feed

Add the Reveal AI NuGet feed to your project using the credentials emailed to you.

```bash
dotnet nuget add source YOUR_FEED_URL \
  --name RevealAI \
  --username YOUR_USERNAME \
  --password YOUR_PASSWORD \
  --store-password-in-clear-text
```

**Verify:**
```bash
dotnet nuget list source
# Should show: RevealAI [Enabled]
```

### 1b. Install the Package

**Using .NET CLI:**

```bash
cd YourProject
dotnet add package Reveal.Sdk.AI.AspNetCore
dotnet build
```

**Using Visual Studio:**

1. Right-click on your project in Solution Explorer
2. Select "Manage NuGet Packages"
3. Select the "Browse" tab
4. **Check the "Include prerelease" checkbox** (required for preview packages)
5. Search for `Reveal.Sdk.AI.AspNetCore`
6. Click "Install"

### 1c. Optional: Install Client-Side Package

If using the JavaScript API:

```bash
npm install @revealbi/api
```

See the [@revealbi/api npm package README](https://www.npmjs.com/package/@revealbi/api) for client-side usage.

---

## Step 2: Configure LLM Provider

Choose **OpenAI** (recommended for quick setup) or **Anthropic Claude**.

### Option A: OpenAI (Recommended)

**Get API Key:**
1. Visit [OpenAI Platform](https://platform.openai.com/)
2. Create an API key (starts with `sk-`)

**Configure in `appsettings.json`:**

```json
{
  "RevealAI": {
    "DefaultClient": "openai",
    "OpenAI": {
      "ApiKey": "sk-your-api-key-here",
      "ModelId": "gpt-4.1"
    }
  }
}
```

### Option B: Anthropic Claude

**Get API Key:**
1. Visit [Anthropic Console](https://platform.anthropic.com/)
2. Create an API key (starts with `sk-ant-`)

**Configure in `appsettings.json`:**

```json
{
  "RevealAI": {
    "DefaultClient": "anthropic",
    "Anthropic": {
      "ApiKey": "sk-ant-your-api-key-here",
      "ModelId": "claude-sonnet-4-5"
    }
  }
}
```

**Tip**: Store your API key in [User Secrets](https://learn.microsoft.com/en-us/aspnet/core/security/app-secrets) rather than committing it to source control.

---

## Step 3: Register AI Services

Update your `Program.cs`:

```csharp
using Reveal.Sdk.AI;

var builder = WebApplication.CreateBuilder(args);

// Your existing Reveal SDK setup
builder.Services.AddControllers()
    .AddReveal(revealBuilder =>
    {
        revealBuilder
            .AddAuthenticationProvider<AuthenticationProvider>()
            .AddDataSourceProvider<DataSourceProvider>()
            .AddUserContextProvider<UserContextProvider>();
    });

// Add Reveal AI services
builder.Services.AddRevealAI();

var app = builder.Build();

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();
```

---

## Step 4: Configure Metadata Generation

The AI needs metadata about your datasources. Add to `appsettings.json`:

```json
{
  "RevealAI": {
    "DefaultClient": "openai",
    "OpenAI": {
      "ApiKey": "OPENAI_API_KEY",
      "ModelId": "gpt-4-turbo"
    },

    "MetadataService": {
      "GenerateOnStartup": true
    },

    "MetadataManager": {
      "Datasources": [
        {
          "id": "my-datasource-id",
          "provider": "SQLServer"
        }
      ]
    }
  }
}
```

**Supported Providers:**
SqlServer, MySQL, Oracle, Postgres, Snowflake, Athena, AnalysisServices, and WebService.

---

## Step 5: Run and Verify

Start your application:

```bash
dotnet run
```

Watch console output for metadata generation:

```
MetadataGenerationHostedService starting
Triggering metadata initialization on startup
...
Generating metadata for datasource my-datasource-id
Enriching metadata for datasource my-datasource-id
...
Metadata initialization completed. Metadata is now ready.
Startup metadata initialization completed
```

**Verify metadata files were created:**

```bash
# Windows
dir %USERPROFILE%\.reveal\ai\metadata\

# Linux/Mac
ls ~/.reveal/ai/metadata/
```

You should see files like:
- `my-datasource-id_index.json`
- `my-datasource-id_MyDB_Orders.json`
- etc.

---

## Step 6: Test Dashboard Generation (Server-Side)

Test the AI dashboard generation endpoint:

**Using curl:**

```bash
curl -X POST http://localhost:5000/api/reveal/ai/dashboards \
  -H "Content-Type: application/json" \
  -d "{\"userPrompt\": \"Show me total sales by region\", \"datasourceId\": \"my-datasource-id\"}"
```

**Expected Response:**

```json
{
  "dashboard": "{...dashboard JSON...}",
  "explanation": "I've created a dashboard showing total sales by region..."
}
```

---

## Step 7: Set Up Client-Side API (Optional)

If you want to use the JavaScript/TypeScript API for insights and chat in your web application:

### 7a. Install the Client Package

```bash
npm install @revealbi/api
```

Or use the CDN:

```html
<script src="https://cdn.jsdelivr.net/npm/@revealbi/api/dist/index.umd.js"></script>
```

### 7b. Initialize the Client

**TypeScript/JavaScript:**

```typescript
import { RevealSdkClient } from '@revealbi/api';

// Initialize once at app startup
RevealSdkClient.initialize({
  hostUrl: 'http://localhost:5000'
});

const client = RevealSdkClient.getInstance();
```

### 7c. Use Chat Interface

```typescript
// Non-streaming: send a message and wait for the complete response
const response = await client.ai.chat.sendMessage({
  message: 'Show me total sales by region',
  datasourceId: 'my-datasource-id',
});

console.log('AI Response:', response.explanation);
if (response.dashboard) {
  // Load the generated/modified dashboard
  loadDashboard(response.dashboard);
}

// Streaming: get real-time text chunks
const stream = await client.ai.chat.sendMessage({
  message: 'Create a dashboard showing revenue trends',
  datasourceId: 'my-datasource-id',
  stream: true,
});

stream.on('progress', (message) => console.log('Status:', message));
stream.on('text', (content) => appendToUI(content));
stream.on('error', (error) => console.error(error));

const result = await stream.finalResponse();
if (result.dashboard) {
  loadDashboard(result.dashboard);
}

// Editing an existing dashboard
const editResponse = await client.ai.chat.sendMessage({
  message: 'Add a date filter to this dashboard',
  datasourceId: 'my-datasource-id',
  dashboard: revealView.dashboard,
});

// Reset conversation context
await client.ai.chat.resetContext();
```

### 7d. Get AI Insights

```typescript
// Non-streaming: get a summary for a dashboard
const insight = await client.ai.insights.get({
  dashboardId: 'my-dashboard',
  type: 'summary',  // 'summary' | 'analysis' | 'forecast'
});

console.log('Insight:', insight.explanation);

// Streaming: get real-time text chunks
const stream = await client.ai.insights.get({
  dashboard: revealView.dashboard,
  type: 'analysis',
  stream: true,
});

stream.on('text', (content) => appendToUI(content));

const result = await stream.finalResponse();
console.log('Complete:', result.explanation);

// Visualization-level insight
const vizInsight = await client.ai.insights.get({
  dashboard: revealView.dashboard,
  visualizationId: 'sales-chart',
  type: 'analysis',
});

// Forecast with custom periods
const forecast = await client.ai.insights.get({
  dashboardId: 'my-dashboard',
  type: 'forecast',
  forecastPeriods: 12,
});
```

**For complete API documentation and advanced usage**, see the [@revealbi/api npm package README](https://www.npmjs.com/package/@revealbi/api).

---

## Success Checklist

- [x] NuGet feed configured with credentials emailed to you
- [x] NuGet package `Reveal.Sdk.AI.AspNetCore` installed
- [x] LLM provider configured (OpenAI or Anthropic)
- [x] `AddRevealAI()` registered in Program.cs
- [x] Application builds without errors
- [x] Metadata files generated in `~/.reveal/ai/metadata/`
- [x] POST to `/api/reveal/ai/dashboards` returns dashboard JSON
- [x] No errors in console logs

---

**Questions?** Contact your Reveal sales representative
