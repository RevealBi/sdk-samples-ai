package com.revealbi.samples.insights.reveal;

import io.revealbi.core.IRVDashboardProvider;
import io.revealbi.core.IRVUserContext;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class DashboardProvider implements IRVDashboardProvider {

    @Override
    public InputStream getDashboard(IRVUserContext userContext, String dashboardId) throws IOException {
        Path dashboardPath = Path.of("Dashboards", dashboardId + ".rdash");
        return new FileInputStream(dashboardPath.toFile());
    }

    @Override
    public void saveDashboard(IRVUserContext userContext, String dashboardId, InputStream dashboardStream) throws IOException {
        Path dashboardPath = Path.of("Dashboards", dashboardId + ".rdash");
        Files.createDirectories(dashboardPath.getParent());
        Files.copy(dashboardStream, dashboardPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
