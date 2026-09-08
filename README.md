# Getting Started with Reveal AI Add-On

This guide will walk you through setting up the Reveal AI Add-On in your existing Reveal SDK application.

**Time to Complete**: 30-45 minutes

---

## Choose Your Platform

| Platform | Package | Minimum Reveal Version |
|---|---|---|
| [ASP.NET Core (C#)](#aspnet-core-c) | `Reveal.Sdk.AI.AspNetCore` (NuGet) | 2.2.0+ |
| [Node.js](#nodejs) | `reveal-sdk-node-ai` (npm) | 2.2.0+ |
| [Java](#java) | `io.revealbi:reveal-sdk-ai` (Maven) | 2.2.0+ |

---

## How AI configuration works: providers and profiles

The AI add-on is configured declaratively with **providers** and **profiles**:

- A **provider** is a named connection to an LLM vendor — its `type` (`OpenAI`, `AzureOpenAI`, `Anthropic`, `Google`, `OpenAICompatible`) plus credentials (`apiKey`, `endpoint`, …). It carries *no* model.
- A **profile** is a named choice of **which provider to use and which model** (plus optional sampling settings like `temperature`). Profiles are what clients and the engine select.
- **`defaultProfile`** names the profile used when a request doesn't specify one.

So a minimal setup is: one provider (`openai`) + one profile (`gpt-4.1`, pointing at that provider and model) set as the default. You can register several providers and many profiles (e.g. a fast model and a reasoning model) and let requests pick per call. The examples below each set up the minimal one-provider/one-profile case.

> **Migrating from the pre-2.2 shape?** The old `defaultProvider` + `settings` (Node/Java) and `RevealAI:DefaultClient` + `RevealAI:<Provider>` (C#) style still works but is deprecated. Move the vendor connection to a **provider** and the model to a **profile** as shown below.

---

## ASP.NET Core (C#)

### Prerequisites

- ✅ **Reveal SDK v2.2.0+** installed and working in your ASP.NET Core app
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

### Step 2: Register AI Services

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

// Add Reveal AI services: register the OpenAI provider connection, define a profile
// (provider + model) named "gpt-4.1", and make it the default.
builder.Services.AddRevealAI()
  .AddOpenAI(openai => openai.ApiKey = builder.Configuration["RevealAI:OpenAI:ApiKey"])
  .AddProfile("gpt-4.1", profile =>
  {
      profile.Provider = "openai";
      profile.Model = "gpt-4.1";
  })
  .SetDefaultProfile("gpt-4.1")
  .UseMetadataCatalogFile("config/catalog.json");

var app = builder.Build();

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();
```

---

### Step 3: Configure Metadata Generation

The AI needs metadata about your datasources. Add to `appsettings.json`:

```json
{
  "RevealAI": {
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

### Step 4: Configure LLM Provider

The provider connection and profile are declared in code (Step 2); the only secret is the API key, which is read from configuration. Choose **OpenAI** (recommended for quick setup) or **Anthropic Claude**.

#### Option A: OpenAI (Recommended)

**Get API Key:**
1. Visit [OpenAI Platform](https://platform.openai.com/)
2. Create an API key (starts with `sk-`)

**Add the key to `appsettings.json`** (Step 2 reads `RevealAI:OpenAI:ApiKey`):

```json
{
  "RevealAI": {
    "OpenAI": {
      "ApiKey": "sk-your-api-key-here"
    }
  }
}
```

The model (`gpt-4.1`) lives on the profile in `Program.cs`, not here.

#### Option B: Anthropic Claude

**Get API Key:**
1. Visit [Anthropic Console](https://platform.anthropic.com/)
2. Create an API key (starts with `sk-ant-`)

Register the Anthropic provider and point a profile at it (in `Program.cs`):

```csharp
builder.Services.AddRevealAI()
  .AddAnthropic(anthropic => anthropic.ApiKey = builder.Configuration["RevealAI:Anthropic:ApiKey"])
  .AddProfile("claude", profile =>
  {
      profile.Provider = "anthropic";
      profile.Model = "claude-sonnet-4-5";
  })
  .SetDefaultProfile("claude")
  .UseMetadataCatalogFile("config/catalog.json");
```

```json
{
  "RevealAI": {
    "Anthropic": {
      "ApiKey": "sk-ant-your-api-key-here"
    }
  }
}
```

**Tip**: Store your API key in [User Secrets](https://learn.microsoft.com/en-us/aspnet/core/security/app-secrets) rather than committing it to source control.

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
curl -X GET http://localhost:5112/api/reveal/ai/metadata/status
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

- ✅ **Reveal 2.2.0+** (`reveal-sdk-node`) installed and working
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

### Step 2: Register the Plugin

Add the AI plugin to your `RevealOptions` with a `providers` map (vendor connections), a `profiles` map (provider + model), and a `defaultProfile`. The metadata catalog referenced here is configured in Step 3, and provider details are described in Step 4.

```javascript
const reveal = require('reveal-sdk-node');
const revealAI = require('reveal-sdk-node-ai');
const path = require('path');
const os = require('os');

const revealOptions = {
  // ... your existing Reveal options
  plugins: [
    revealAI.withOptions({
      defaultProfile: 'gpt-4.1',
      providers: {
        openai: { type: 'OpenAI', apiKey: process.env.OPENAI_API_KEY }
      },
      profiles: {
        'gpt-4.1': { provider: 'openai', model: 'gpt-4.1' }
      },
      metadataCatalogFile: path.resolve(__dirname, 'Reveal', 'Metadata', 'catalog.json'),
      metadataManager: {
        outputPath: path.resolve(os.homedir(), 'AImetadata'),
      }
    })
  ]
};
```

---

### Step 3: Configure Metadata

Create a metadata catalog JSON file listing your datasources (same format as C#):

```json
{
  "Datasources": [
    {
      "Id": "my-datasource-id",
      "Provider": "SQLServer"
    }
  ]
}
```

**Supported Providers:** AmazonAthena, MySQL, Oracle, OracleSID, PostgreSQL, SSAS, SSASHTTP, Snowflake, SQLServer, WebService

---

### Step 4: Configure LLM Provider

Declare the vendor connection under `providers` (credentials + `type`) and the model under `profiles`. The `type` selects the built-in adapter; the API key is best loaded from an environment variable.

#### Option A: OpenAI (Recommended)

```javascript
revealAI.withOptions({
  defaultProfile: 'gpt-4.1',
  providers: { openai: { type: 'OpenAI', apiKey: process.env.OPENAI_API_KEY } },
  profiles:  { 'gpt-4.1': { provider: 'openai', model: 'gpt-4.1' } },
  // ...metadataCatalogFile, metadataManager
});
```

#### Option B: Anthropic Claude

```javascript
revealAI.withOptions({
  defaultProfile: 'claude',
  providers: { anthropic: { type: 'Anthropic', apiKey: process.env.ANTHROPIC_API_KEY } },
  profiles:  { claude: { provider: 'anthropic', model: 'claude-sonnet-4-5' } },
  // ...metadataCatalogFile, metadataManager
});
```

**Tip**: Load the API key from a secure source (environment variables, a secrets manager, or a local config file) and pass it at startup.

---

### Step 5: Run and Verify

```bash
node server.js
```

Once running, verify the AI endpoint:

```bash
curl -X GET http://localhost:5112/api/reveal/ai/metadata/status
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

- ✅ **Reveal 2.2.0+** (`io.revealbi:reveal-sdk-servlet` or Spring equivalent) installed and working
- ✅ **Java 17+**
- ✅ **Maven 3.6+**
- ✅ **LLM Provider account** (OpenAI or Anthropic recommended)
- ✅ At least one datasource configured in Reveal SDK

### Step 1: Add Maven Dependency

Add the Reveal Maven repository and dependency to your `pom.xml`:

```xml
<repositories>
  <repository>
    <id>reveal.public</id>
    <url>https://maven.revealbi.io/repository/public</url>
  </repository>
</repositories>

<dependencies>
  <!-- The base Reveal Java SDK must be 2.2.0+ to match the AI plugin's engine -->
  <dependency>
    <groupId>io.revealbi</groupId>
    <artifactId>reveal-sdk-servlet</artifactId>
    <version>2.2.0</version>
  </dependency>
  <dependency>
    <groupId>io.revealbi</groupId>
    <artifactId>reveal-sdk-ai</artifactId>
    <version>1.2.0</version>
  </dependency>
</dependencies>
```

> **Important:** keep `reveal-sdk-servlet` at **2.2.0+**. The AI plugin ships an engine built for 2.2.0; an older base SDK (e.g. 2.1.0) pulls a mismatched engine and the plugin fails to load at startup.

Then run:

```bash
mvn install
```

---

### Step 2: Register the Plugin

Add the AI plugin when building your `RevealServer`, composing options with `RevealAIPluginOptions.builder()`:
- `addProvider(name, options)` – a vendor connection; `options` holds the `"type"` (`"OpenAI"`, `"Anthropic"`, …) and credentials (`"apiKey"`, …)
- `addProfile(name, options)` – a profile referencing a provider (`"provider"`) and a `"model"`
- `defaultProfile(name)` – the profile used when a request names none
- `metadataCatalogFile(...)` – path to your catalog JSON, configured in Step 3
- `metadataManager(...)` – output directory for generated metadata

```java
import io.revealbi.ai.RevealAIPlugin;
import io.revealbi.ai.RevealAIPluginOptions;
import io.revealbi.core.IRevealServer;
import io.revealbi.core.RevealServerBuilder;

import java.nio.file.Path;
import java.util.Map;

RevealAIPluginOptions aiPluginOptions = RevealAIPluginOptions.builder()
    .defaultProfile("gpt-4.1")
    .addProvider("openai", Map.of("type", "OpenAI", "apiKey", System.getenv("OPENAI_API_KEY")))
    .addProfile("gpt-4.1", Map.of("provider", "openai", "model", "gpt-4.1"))
    .metadataCatalogFile(Path.of("src", "main", "resources", "Reveal", "Metadata", "catalog.json")
        .toAbsolutePath().normalize().toString())
    .metadataManager(new RevealAIPluginOptions.MetadataManagerOptions(
        Path.of(System.getProperty("user.home"), "AImetadata").toString()))
    .build();

IRevealServer revealServer = new RevealServerBuilder()
    .setDataSourceProvider(dataSourceProvider)
    .addPlugin(RevealAIPlugin.withOptions(aiPluginOptions))
    .build();
```

---

### Step 3: Configure Metadata

Create a metadata catalog JSON file listing your datasources (same format as C#):

```json
{
  "Datasources": [
    {
      "Id": "my-datasource-id",
      "Provider": "SQLServer"
    }
  ]
}
```

**Supported Providers:** AmazonAthena, MySQL, Oracle, OracleSID, PostgreSQL, SSAS, SSASHTTP, Snowflake, SQLServer, WebService

---

### Step 4: Configure LLM Provider

Declare the vendor connection with `addProvider(...)` (credentials + `"type"`) and the model with `addProfile(...)`. The `"type"` selects the built-in adapter; load the API key from a secure source.

#### Option A: OpenAI (Recommended)

```java
RevealAIPluginOptions.builder()
    .defaultProfile("gpt-4.1")
    .addProvider("openai", Map.of("type", "OpenAI", "apiKey", System.getenv("OPENAI_API_KEY")))
    .addProfile("gpt-4.1", Map.of("provider", "openai", "model", "gpt-4.1"))
    // ...metadataCatalogFile, metadataManager
    .build();
```

#### Option B: Anthropic Claude

```java
RevealAIPluginOptions.builder()
    .defaultProfile("claude")
    .addProvider("anthropic", Map.of("type", "Anthropic", "apiKey", System.getenv("ANTHROPIC_API_KEY")))
    .addProfile("claude", Map.of("provider", "anthropic", "model", "claude-sonnet-4-5"))
    // ...metadataCatalogFile, metadataManager
    .build();
```

**Tip**: Load the API key from a secure source (environment variables, a secrets manager, or a local config file) and pass it at startup.

---

### Step 5: Run and Verify

```bash
mvn spring-boot:run
```

Once running, verify the AI endpoint:

```bash
curl -X GET http://localhost:5112/api/reveal/ai/metadata/status
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
  hostUrl: 'http://localhost:5112'
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
- [ ] `AddRevealAI()` registered in `Program.cs`
- [ ] Metadata catalog configured (datasource list)
- [ ] LLM provider configured in `appsettings.json` (OpenAI or Anthropic)
- [ ] Application builds without errors
- [ ] Metadata files generated in `reveal/ai/metadata/`
- [ ] `GET /api/reveal/ai/metadata/status` returns `isInitialized: true`

### Node.js

- [ ] `reveal-sdk-node-ai` npm package installed
- [ ] `revealAI.withOptions(...)` added to `RevealOptions.plugins`
- [ ] Metadata catalog JSON file configured with datasource list
- [ ] `providers` map declares each vendor connection (`type` + `apiKey`)
- [ ] `profiles` map declares provider + model; `defaultProfile` set in `withOptions()`
- [ ] Application starts without errors
- [ ] `GET /api/reveal/ai/metadata/status` returns `isInitialized: true`

### Java

- [ ] `io.revealbi:reveal-sdk-ai` (1.2.0+) Maven dependency added, with `reveal-sdk-servlet` at **2.2.0+**
- [ ] `RevealAIPlugin.withOptions(aiPluginOptions)` added via `RevealServerBuilder.addPlugin()`
- [ ] Metadata catalog JSON file configured with datasource list
- [ ] `addProvider(...)` declares each vendor connection (`"type"` + `"apiKey"`)
- [ ] `addProfile(...)` declares provider + model; `defaultProfile(...)` set on the builder
- [ ] Application builds and starts without errors
- [ ] `GET /api/reveal/ai/metadata/status` returns `isInitialized: true`

---
