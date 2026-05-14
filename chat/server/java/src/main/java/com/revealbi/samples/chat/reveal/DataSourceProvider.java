package com.revealbi.samples.chat.reveal;

import io.revealbi.core.IRVUserContext;
import io.revealbi.core.data.IRVDataSourceProvider;
import io.revealbi.core.data.RVDashboardDataSource;
import io.revealbi.core.data.RVDataSourceItem;
import io.revealbi.core.data.RVWebResourceDataSource;
import org.springframework.stereotype.Component;

@Component
public class DataSourceProvider implements IRVDataSourceProvider {

    @Override
    public RVDataSourceItem changeDataSourceItem(IRVUserContext userContext, String dashboardId, RVDataSourceItem dataSourceItem) {
        changeDataSource(userContext, dataSourceItem.getDataSource());
        return dataSourceItem;
    }

    @Override
    public RVDashboardDataSource changeDataSource(IRVUserContext userContext, RVDashboardDataSource dataSource) {
        if ("SampleExcel".equals(dataSource.getId()) && dataSource instanceof RVWebResourceDataSource webResource) {
            webResource.setUseAnonymousAuthentication(true);
            webResource.setUrl("https://download.infragistics.com/reveal/sampledata/SamplesIA.xlsx");
        }
        return dataSource;
    }
}
