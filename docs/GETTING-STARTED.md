# Getting Started with Reveal AI Add-On (Private Preview)

This guide will walk you through setting up the Reveal AI Add-On in your existing Reveal SDK application.

> **⚠️ PRIVATE PREVIEW**: This is pre-release software for evaluation only. Not intended for production use. Breaking changes expected before RTM.

**Time to Complete**: 30-45 minutes

---

## Prerequisites

- ✅ **Private preview access** granted by your Reveal sales representative
- ✅ **NuGet package zip file** provided by Reveal
- ✅ **Reveal SDK v1.8.3+** installed and working in your ASP.NET Core app
- ✅ **.NET 8.0 SDK** installed
- ✅ **LLM Provider account** (OpenAI or Anthropic recommended)
- ✅ At least one datasource configured in Reveal SDK

---

## Step 1: Install NuGet Package

### 1a. Extract the Package

Extract the zip file provided by your sales rep to a local folder:

```bash
# Windows
mkdir C:\RevealAI-NuGet
# Extract Reveal.Sdk.AI.AspNetCore.0.1.27-preview.nupkg to this folder

# Linux/Mac
mkdir ~/RevealAI-NuGet
# Extract Reveal.Sdk.AI.AspNetCore.0.1.27-preview.nupkg to this folder
```

### 1b. Add Local NuGet Source

```bash
# Windows
dotnet nuget add source C:\RevealAI-NuGet --name RevealAI-Local

# Linux/Mac
dotnet nuget add source ~/RevealAI-NuGet --name RevealAI-Local
```

**Verify:**
```bash
dotnet nuget list source
# Should show: RevealAI-Local [Enabled]
```

### 1c. Install the Package

```bash
cd YourProject
dotnet add package Reveal.Sdk.AI.AspNetCore --version 0.1.27-preview
dotnet build
```

### 1d. Optional: Install Client-Side Package

If using the JavaScript API:

```bash
npm install @revealbi/api@0.0.1-preview.2
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
      "ModelId": "gpt-4.1"
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
SqlServer, MySQL, Oracle, Postgres, Snowflake, Athena, AnalysisServices, WebService, Excel and CSV.

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
npm install @revealbi/api@latest
```

### 7b. Initialize the Client

**TypeScript/JavaScript:**

```typescript
import { RevealSdkClient } from '@revealbi/api';

// Initialize the client with your server endpoint
const client = new RevealSdkClient({
  serverUrl: 'http://localhost:5000'
});
```

### 7c. Generate Dashboards

```typescript
// Generate a dashboard from natural language
const result = await client.ai.dashboards.generate({
  userPrompt: 'Show me total sales by region',
  datasourceId: 'my-datasource-id'
});

console.log('Dashboard JSON:', result.dashboard);
console.log('Explanation:', result.explanation);
```

### 7d. Use Chat Interface

```typescript
// Send a chat message
const chatResponse = await client.ai.chat.sendMessage({
  question: 'What were the top 5 products last quarter?',
  datasourceId: 'my-datasource-id',
  dashboard: revealView.dashboard //the current dashboard (optional)
});

console.log('AI Response:', chatResponse.explanation);
console.log('AI Dashboard:', chatResponse.dashboard);
```

### 7e. Get Widget Insights

```typescript
// Analyze an existing widget for insights
const insights = await client.ai.insights.get({
  widgetId: 'widget-123',
  dashboardId: 'dashboard-abc'
});

console.log('Insights:', insights);
```

### 7f. List Available Datasources

```typescript
// Get list of datasources
const datasources = await client.ai.datasources.list();

datasources.forEach(ds => {
  console.log(`${ds}`);
});
```

### 7g. Check Metadata Status

```typescript
// Check metadata generation status for a datasource
const status = await client.ai.metadata.getStatus();

console.log('Status:', status.status);
```

**For complete API documentation and advanced usage**, see the [@revealbi/api npm package README](https://www.npmjs.com/package/@revealbi/api).

---

## Success Checklist

- [x] NuGet package `Reveal.Sdk.AI.AspNetCore` installed (v0.1.27-preview)
- [x] LLM provider configured (OpenAI or Anthropic)
- [x] `AddRevealAI()` registered in Program.cs
- [x] Application builds without errors
- [x] Metadata files generated in `~/.reveal/ai/metadata/`
- [x] POST to `/api/reveal/ai/dashboards` returns dashboard JSON
- [x] No errors in console logs

---

**Questions?** Contact your Reveal sales representative
