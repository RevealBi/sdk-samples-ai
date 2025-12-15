# Insights Sample - Quick Start Guide

This sample demonstrates the Reveal SDK AI insights functionality.

## Prerequisites

Set the following configuration in your user secrets. Only configure the AI model you intend to use:

```json
{
    "OpenAI": {
        "ApiKey": "<key>",
        "ModelId": "<model id>"
    },
    "Anthropic": {
        "ApiKey": "<key>",
        "ModelId": "<model id>"
    },
    "Google": {
        "CredentialsPath": "<path to service account credentials in json format>",
        "ProjectId": "<google project id>",
        "Location": "<region where the model is hosted, default: us-central1>",
        "Publisher": "<model publisher, default: google>",
        "ModelId": "<model id, default: gemini-2.5-pro>",
    }
}
```

You can also override/set these options during setup, like so:

```
    builder.Services.AddRevealAI().ConfigureOpenAI(settings =>
    {
        settings.ApiKey = builder.Configuration["RevealAI:OpenAI:ApiKey"];
        settings.ModelId = "gpt-4.1";
    });
```

## Getting Started

1. **Build and launch the server**
   
   Navigate to the server directory and run the ASP.NET application.

2. **Open the client**
   
   Open `client/index.html` in your preferred browser.