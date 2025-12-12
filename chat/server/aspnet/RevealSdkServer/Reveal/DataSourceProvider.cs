using Reveal.Sdk;
using Reveal.Sdk.Data;

namespace RevealSdkServer.Reveal
{
    public class DataSourceProvider: IRVDataSourceProvider
    {
        public Task<RVDataSourceItem> ChangeDataSourceItemAsync(IRVUserContext userContext, string dashboardId, RVDataSourceItem dataSourceItem)
        {
            throw new NotImplementedException();
        }

        public Task<RVDashboardDataSource> ChangeDataSourceAsync(IRVUserContext userContext, RVDashboardDataSource dataSource)
        {
            throw new NotImplementedException();
        }
    }
}
