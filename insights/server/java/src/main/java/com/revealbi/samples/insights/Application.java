package com.revealbi.samples.insights;

import io.revealbi.ai.RevealAIPlugin;
import io.revealbi.ai.RevealAIPluginOptions;
import io.revealbi.core.IRevealServer;
import io.revealbi.core.RevealPluginCallback;
import io.revealbi.core.RevealServerBuilder;
import io.revealbi.servlet.RevealEngineServlet;
import com.revealbi.samples.insights.reveal.DashboardProvider;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public IRevealServer revealServer(DashboardProvider dashboardProvider) {
        // AI provider settings – replace with your own API key or load from env
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null) apiKey = "YOUR_API_KEY_HERE";

        Map<String, Object> aiSettings = Map.of(
                "openai", Map.of("ApiKey", apiKey, "Model", "gpt-4.1")
        );

        RevealAIPluginOptions aiPluginOptions = new RevealAIPluginOptions(
                "openai",
                Path.of("src", "main", "resources", "Reveal", "Metadata", "catalog.json")
                        .toAbsolutePath().normalize().toString(),
                new RevealAIPluginOptions.MetadataManagerOptions(
                        Path.of(System.getProperty("user.home"), "AImetadata").toString()),
                null,
                Map.of("settings", aiSettings));

        // Dummy callbacks to prevent crashes when the plugin invokes them
        Map<String, RevealPluginCallback> callbacks = Map.of(
                "contextManagerProvider", (userContext, message) ->
                        CompletableFuture.completedFuture(""),
                "aiProvider", (userContext, message) ->
                        CompletableFuture.completedFuture("")
        );

        return new RevealServerBuilder()
                .addSettings(settings -> settings.setLocalFilesStoragePath(
                        Path.of("Data").toAbsolutePath().normalize().toString()))
                .setDashboardProvider(dashboardProvider)
                .addPlugin(RevealAIPlugin.withOptions(aiPluginOptions, callbacks))
                .build();
    }

    @Bean
    JettyServletWebServerFactory jettyFactory() {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.addServerCustomizers(server -> {
            for (var connector : server.getConnectors()) {
                connector.getConnectionFactories().stream()
                        .filter(cf -> cf instanceof HttpConnectionFactory)
                        .map(cf -> (HttpConnectionFactory) cf)
                        .forEach(httpCf -> {
                            httpCf.getHttpConfiguration().setOutputBufferSize(512);
                            httpCf.getHttpConfiguration().setOutputAggregationSize(0);
                        });
            }
        });
        return factory;
    }

    @Bean
    ServletRegistrationBean<RevealEngineServlet> revealServlet(IRevealServer revealServer) {
        ServletRegistrationBean<RevealEngineServlet> registration =
                new ServletRegistrationBean<>(new RevealEngineServlet(revealServer), "/*");
        registration.setAsyncSupported(true);
        registration.setLoadOnStartup(1);
        return registration;
    }

    @EventListener
    public void onClose(ContextClosedEvent event) {
        IRevealServer revealServer = event.getApplicationContext().getBean(IRevealServer.class);
        try {
            revealServer.shutdown();
        } catch (Exception ex) {
            System.err.println("Failed to shut down Reveal server: " + ex.getMessage());
        }
    }
}
