using Reveal.Sdk;
using Reveal.Sdk.Data;

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
            return Task.FromResult(dataSource);
        }
    }
}
