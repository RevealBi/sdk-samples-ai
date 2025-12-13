using Reveal.Sdk;
using Reveal.Sdk.Data;
using Reveal.Sdk.Data.Rest;

namespace RevealSdkServer.Reveal
{
    public class DataSourceProvider : IRVDataSourceProvider
    {
        public Task<RVDataSourceItem> ChangeDataSourceItemAsync(IRVUserContext userContext, string dashboardId, RVDataSourceItem dataSourceItem)
        {
            ChangeDataSourceAsync(userContext, dataSourceItem.DataSource);

            return Task.FromResult(dataSourceItem);
        }

        public Task<RVDashboardDataSource> ChangeDataSourceAsync(IRVUserContext userContext, RVDashboardDataSource dataSource)
        {
            if (dataSource.Id == "SampleExcel" && dataSource is RVWebResourceDataSource rvWebResourceDataSource)
            {
                rvWebResourceDataSource.UseAnonymousAuthentication = true;
                rvWebResourceDataSource.Url = "https://download.infragistics.com/reveal/sampledata/SamplesIA.xlsx";
            }

            return Task.FromResult(dataSource);
        }
    }

    public class UserContextProvider : IRVUserContextProvider
    {
        public IRVUserContext GetUserContext(HttpContext aspnetContext)
        {
            // For demonstration purposes, we return a simple user context.
            IRVUserContext userContext = new RVUserContext("DemoUser");
            return userContext;
        }
    }
}
