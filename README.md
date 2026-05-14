# Getting Started with Reveal AI Add-On

This guide will walk you through setting up the Reveal AI Add-On in your existing Reveal SDK application.

**Time to Complete**: 30-45 minutes

---

## Choose Your Platform

| Platform | Package | Minimum Reveal Version |
|---|---|---|
| [ASP.NET Core (C#)](#aspnet-core-c) | `Reveal.Sdk.AI.AspNetCore` (NuGet) | 1.8.4+ |
| [Node.js](#nodejs) | `reveal-sdk-node-ai` (npm) | 2.0.0+ |
| [Java](#java) | `io.revealbi:reveal-sdk-ai` (Maven) | 2.0.0+ |

---

## ASP.NET Core (C#)

### Prerequisites

- ✅ **Reveal SDK v1.8.4+** installed and working in your ASP.NET Core app
- ✅ **.NET 8.0 SDK** installed
- ✅ **LLM Provider account** (OpenAI or Anthropic recommended)
- ✅ At least one datasource configured in Reveal SDK

### Step 1a: Install NuGet Package

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
4. Search for `Reveal.Sdk.AI.AspNetCore`
5. Click "Install"

#### 1b. Optional: Install Client-Side Package

If using the JavaScript API:

```bash
npm install @revealbi/api
```

See the [@revealbi/api npm package README](https://www.npmjs.com/package/@revealbi/api) for client-side usage.

---

### Step 2: Configure LLM Provider

Choose **OpenAI** (recommended for quick setup) or **Anthropic Claude**.

#### Option A: OpenAI (Recommended)

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

#### Option B: Anthropic Claude

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

### Step 3: Register AI Services

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
builder.Services.AddRevealAI()
  .AddOpenAI()
  .UseMetadataCatalogFile("config/catalog.json");

var app = builder.Build();

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();
```

---

### Step 4: Configure Metadata Generation

The AI needs metadata about your datasources. Add to `appsettings.json`:

```json
{
  "RevealAI": {
    "OpenAI": {
      "ApiKey": "OPENAI_API_KEY",
      "ModelId": "gpt-4-turbo"
    },

    "MetadataService": {
      "GenerateOnStartup": true
    }
  }
}
```

Then list your configured datasources in your metadata catalog file (e.g. `config/catalog.json`):

```json
{
  "Datasources": [
    {
      "id": "my-datasource-id",
      "provider": "SQLServer"
    }
  ]
}
```

**Supported Providers:** AmazonAthena, MySQL, Oracle, OracleSID, PostgreSQL, SSAS, SSASHTTP, Snowflake, SQLServer, WebService

---

### Step 5: Run and Verify

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
dir %localappdata%\reveal\ai\metadata\

# Linux
ls ~/.local/share/reveal/ai/metadata/

# Mac
ls ~/Library/Application\ Support/reveal/ai/metadata
```

You should see files like:
- `my-datasource-id_index.json`
- `my-datasource-id_MyDB_Orders.json`
- etc.

---

### Step 6: Test Dashboard Generation (Server-Side)

```bash
curl -X GET http://localhost:5000/api/reveal/ai/metadata/status
```

**Expected Response (once system is ready):**

```json
{
  "status": "Completed",
  "isInitialized": true
}
```

---

## Node.js

### Prerequisites

- ✅ **Reveal 2.0.0+** (`reveal-sdk-node`) installed and working
- ✅ **Node.js 16+**
- ✅ **LLM Provider account** (OpenAI or Anthropic recommended)
- ✅ At least one datasource configured in Reveal SDK

### Step 1: Install npm Package

```bash
npm install reveal-sdk-node-ai
```

#### Optional: Install Client-Side Package

```bash
npm install @revealbi/api
```

---

### Step 2: Configure LLM Provider

Pass your LLM provider settings via the `settings` option when registering the plugin (see Step 3). The settings object mirrors the `RevealAI` section of the C# `appsettings.json`:

#### Option A: OpenAI (Recommended)

```json
{
  "DefaultClient": "openai",
  "OpenAI": {
    "ApiKey": "sk-your-api-key-here",
    "ModelId": "gpt-4.1"
  }
}
```

#### Option B: Anthropic Claude

```json
{
  "DefaultClient": "anthropic",
  "Anthropic": {
    "ApiKey": "sk-ant-your-api-key-here",
    "ModelId": "claude-sonnet-4-5"
  }
}
```

**Tip**: Load these settings from a secure source (environment variables, a secrets manager, or a local config file) and pass them at startup.

---

### Step 3: Register the Plugin

Add the AI plugin to your `RevealOptions`, passing the settings object:

```javascript
const reveal = require('reveal-sdk-node');
const revealAI = require('reveal-sdk-node-ai');
const path = require('path');
const os = require('os');

// Load your AI provider settings from your preferred config source
const aiSettings = {
  DefaultClient: 'openai',
  OpenAI: { ApiKey: process.env.OPENAI_API_KEY, ModelId: 'gpt-4.1' }
};

const revealOptions = {
  // ... your existing Reveal options
  plugins: [
    revealAI.withOptions({
      settings: aiSettings,
      metadataCatalogFile: path.resolve(__dirname, 'metadataCatalog.json'),
      metadataManager: {
        outputPath: path.resolve(os.homedir(), 'AImetadata'),
      },
      contextManager: {
        path: __dirname,
        fileName: 'context.json'
      }
    })
  ]
};
```

---

### Step 4: Configure Metadata

Create a `metadataCatalog.json` file listing your datasources (same format as C#):

```json
{
  "Datasources": [
    {
      "id": "my-datasource-id",
      "provider": "SQLServer"
    }
  ]
}
```

**Supported Providers:** AmazonAthena, MySQL, Oracle, OracleSID, PostgreSQL, SSAS, SSASHTTP, Snowflake, SQLServer, WebService

---

### Step 5: Run and Verify

```bash
node server.js
```

Once running, verify the AI endpoint:

```bash
curl -X GET http://localhost:5000/api/reveal/ai/metadata/status
```

**Expected Response:**

```json
{
  "status": "Completed",
  "isInitialized": true
}
```

---

## Java

### Prerequisites

- ✅ **Reveal 2.0.0+** (`io.revealbi:reveal-sdk-servlet` or Spring equivalent) installed and working
- ✅ **Java 17+**
- ✅ **Maven 3.6+**
- ✅ **LLM Provider account** (OpenAI or Anthropic recommended)
- ✅ At least one datasource configured in Reveal SDK

### Step 1: Add Maven Dependency

Add to your `pom.xml`:

```xml
<dependency>
  <groupId>io.revealbi</groupId>
  <artifactId>reveal-sdk-ai</artifactId>
  <version>0.1.0</version>
</dependency>
```

Then run:

```bash
mvn install
```

---

### Step 2: Configure LLM Provider

Pass your LLM provider settings via the `additionalOptions` map when creating `RevealAIPluginOptions` (see Step 3). The settings map mirrors the `RevealAI` section of the C# `appsettings.json`:

#### Option A: OpenAI (Recommended)

```json
{
  "DefaultClient": "openai",
  "OpenAI": {
    "ApiKey": "sk-your-api-key-here",
    "ModelId": "gpt-4.1"
  }
}
```

#### Option B: Anthropic Claude

```json
{
  "DefaultClient": "anthropic",
  "Anthropic": {
    "ApiKey": "sk-ant-your-api-key-here",
    "ModelId": "claude-sonnet-4-5"
  }
}
```

**Tip**: Load these settings from a secure source (environment variables, a secrets manager, or a local config file) and pass them at startup.

---

### Step 3: Register the Plugin

Add the AI plugin when building your `RevealServer`, passing your settings via `additionalOptions`:

```java
import io.revealbi.ai.RevealAIPlugin;
import io.revealbi.ai.RevealAIPluginOptions;
import io.revealbi.core.RevealServerBuilder;

import java.nio.file.Path;
import java.util.Map;

// Load your AI provider settings from your preferred config source
Map<String, Object> aiSettings = Map.of(
    "DefaultClient", "openai",
    "OpenAI", Map.of("ApiKey", System.getenv("OPENAI_API_KEY"), "ModelId", "gpt-4.1")
);

RevealAIPluginOptions aiPluginOptions = new RevealAIPluginOptions(
    Path.of("config/metadataCatalog.json").toAbsolutePath().normalize().toString(),
    new RevealAIPluginOptions.MetadataManagerOptions(
        Path.of(System.getProperty("user.home"), "AImetadata").toString()),
    new RevealAIPluginOptions.ContextManagerOptions(
        Path.of("config").toAbsolutePath().normalize().toString(),
        "context.json"),
    Map.of("settings", aiSettings));

IRevealServer revealServer = new RevealServerBuilder()
    .setAuthenticationProvider(authenticationProvider)
    .setDataSourceProvider(dataSourceProvider)
    .addPlugin(RevealAIPlugin.withOptions(aiPluginOptions))
    .build();
```

---

### Step 4: Configure Metadata

Create a `metadataCatalog.json` file listing your datasources (same format as C#):

```json
{
  "Datasources": [
    {
      "id": "my-datasource-id",
      "provider": "SQLServer"
    }
  ]
}
```

**Supported Providers:** AmazonAthena, MySQL, Oracle, OracleSID, PostgreSQL, SSAS, SSASHTTP, Snowflake, SQLServer, WebService

---

### Step 5: Run and Verify

```bash
mvn spring-boot:run
```

Once running, verify the AI endpoint:

```bash
curl -X GET http://localhost:8080/api/reveal/ai/metadata/status
```

**Expected Response:**

```json
{
  "status": "Completed",
  "isInitialized": true
}
```

---

## Set Up Client-Side API (Optional)

If you want to use the JavaScript/TypeScript API for insights and chat in your web application:

### Install the Client Package

```bash
npm install @revealbi/api
```

Or use the CDN:

```html
<script src="https://cdn.jsdelivr.net/npm/@revealbi/api/dist/index.umd.js"></script>
```

### Initialize the Client

```typescript
import { RevealSdkClient } from '@revealbi/api';

// Initialize once at app startup
RevealSdkClient.initialize({
  hostUrl: 'http://localhost:5000'
});

const client = RevealSdkClient.getInstance();
```

### Use Chat Interface

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

### Get AI Insights

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

### ASP.NET Core (C#)

- [ ] NuGet package `Reveal.Sdk.AI.AspNetCore` installed
- [ ] LLM provider configured in `appsettings.json` (OpenAI or Anthropic)
- [ ] Metadata catalog configured (datasource list)
- [ ] `AddRevealAI()` registered in `Program.cs`
- [ ] Application builds without errors
- [ ] Metadata files generated in `reveal/ai/metadata/`
- [ ] `GET /api/reveal/ai/metadata/status` returns `isInitialized: true`

### Node.js

- [ ] `reveal-sdk-node-ai` npm package installed
- [ ] LLM provider settings passed via `settings` option in `withOptions()`
- [ ] `metadataCatalog.json` configured with datasource list
- [ ] `revealAI.withOptions(...)` added to `RevealOptions.plugins`
- [ ] Application starts without errors
- [ ] `GET /api/reveal/ai/metadata/status` returns `isInitialized: true`

### Java

- [ ] `io.revealbi:reveal-sdk-ai` Maven dependency added
- [ ] LLM provider settings passed via `additionalOptions` in `RevealAIPluginOptions`
- [ ] `metadataCatalog.json` configured with datasource list
- [ ] `RevealAIPlugin.withOptions(...)` added via `RevealServerBuilder.addPlugin()`
- [ ] Application builds and starts without errors
- [ ] `GET /api/reveal/ai/metadata/status` returns `isInitialized: true`

---
