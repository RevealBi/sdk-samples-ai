# Compatibility Matrix

This document outlines the compatibility requirements and supported versions for the Reveal AI Add-On.

## Version Requirements

### Reveal SDK

| Component | Minimum Version | Samples use | Notes |
|-----------|----------------|-------------|-------|
| **Reveal.Sdk.AspNetCore** | 2.3.0 | 2.3.0 | Core SDK for ASP.NET Core |
| **Reveal.Sdk.AI.AspNetCore** | 1.6.358 | 1.6.358 | AI Add-On for ASP.NET Core (NuGet) |
| **reveal-sdk-node** / **reveal-sdk-node-ai** (npm) | 2.3.0 / 1.3.0 | 2.3.0 / 1.3.0 | Node.js server SDK and AI plugin |
| **reveal-sdk-servlet** / **reveal-sdk-ai** (Maven) | 2.3.0 / 1.3.0 | 2.3.0 / 1.3.0 | Java server SDK and AI plugin |
| **reveal-sdk** (npm) | 2.3.0 | 2.3.0 | Client-side `RevealView`, loaded as an ES module |
| **@revealbi/api** (npm) | 1.6.358 | 1.6.358 | Client-side AI API |

**Important**:
- The AI Add-On requires **Reveal SDK 2.3.0 or higher**, on the server and in the browser. Earlier versions are not supported.
- The 2.x client no longer uses jQuery, dayjs or the `$.ig` global. Import `RevealView` and the other classes from `reveal-sdk` instead.

### .NET Framework

**Requirement**: .NET 8.0 SDK

**Installation**:
- Download: https://dotnet.microsoft.com/download/dotnet/8.0
- Verify: `dotnet --version` (should show 8.0.x)

**Note**: Only .NET 8.0 is supported. Earlier versions (.NET 6.0, 7.0) and .NET Framework 4.x are not supported.

---

## Supported Datasources

The AI Add-On requires datasources that support metadata generation. The following datasources are supported:

| Datasource | Provider Name | Notes |
|------------|---------------|-------|
| **Microsoft SQL Server** | `SQLServer` | Also use for Azure SQL Database (`AzureSQL`) |
| **MySQL** | `MySQL` | Full support |
| **Oracle** | `Oracle` or `OracleSID` | Use appropriate provider name for your configuration |
| **PostgreSQL** | `PostgreSQL` | Full support |
| **Snowflake** | `Snowflake` | Full support |
| **Amazon Athena** | `AmazonAthena` | Full support |
| **SQL Server Analysis Services** | `SSAS` or `SSASHTTP` | Use appropriate provider name for your configuration or `AzureAnalysisServices` for Azure |
| **Excel Files** | `Excel` | Single-table scenarios |
| **CSV Files** | `CSV` | Single-table scenarios |

**Important Notes**:
- **Schema Size**: Databases with < 100 tables recommended for best performance
- **Large Databases**: Use metadata whitelisting for databases with 500+ tables
- **File-Based**: Excel and CSV work best for single-table scenarios without joins
- **Other Datasources**: Datasources not listed above may not support metadata generation and may not work with all the AI Add-On features

---

## Compatibility Testing Checklist

Before deploying to production, verify:

### Framework & SDK
- [ ] .NET 8.0 SDK installed
- [ ] Reveal SDK 2.3.0+ referenced (server and client)
- [ ] AI Add-On package installed
- [ ] Application builds without errors

### LLM Providers
- [ ] At least one provider configured
- [ ] API key valid and working
- [ ] Test dashboard generation succeeds
- [ ] Network connectivity to provider endpoint

### Datasources
- [ ] Datasource supported by Reveal SDK
- [ ] Metadata generation succeeds
- [ ] Field types correctly detected
- [ ] Join relationships work (if SQL)

### Deployment Environment
- [ ] OS supported (.NET 8.0 compatible)
- [ ] Network connectivity
- [ ] Sufficient resources (2+ CPU cores, 4+ GB RAM recommended)

### Browser (For Viewer)
- [ ] Modern browser available (Chrome, Edge, Firefox, Safari)
- [ ] JavaScript enabled
- [ ] Cookies enabled (for authentication)
