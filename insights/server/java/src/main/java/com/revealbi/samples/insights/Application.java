package com.revealbi.samples.insights;

import io.revealbi.ai.RevealAIPlugin;
import io.revealbi.ai.RevealAIPluginOptions;
import io.revealbi.core.IRevealServer;
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

        // Declarative "profiles" configuration: a named provider connection (openai) carries
        // the credentials; a profile (gpt-4.1) selects the model and is used as the default.
        RevealAIPluginOptions aiPluginOptions = RevealAIPluginOptions.builder()
                .defaultProfile("gpt-4.1")
                .addProvider("openai", Map.of("type", "OpenAI", "apiKey", apiKey))
                .addProfile("gpt-4.1", Map.of("provider", "openai", "model", "gpt-4.1"))
                .metadataCatalogFile(Path.of("src", "main", "resources", "Reveal", "Metadata", "catalog.json")
                        .toAbsolutePath().normalize().toString())
                .metadataManager(new RevealAIPluginOptions.MetadataManagerOptions(
                        Path.of(System.getProperty("user.home"), "AImetadata").toString()))
                .build();

        return new RevealServerBuilder()
                .addSettings(settings -> settings.setLocalFilesStoragePath(
                        Path.of("Data").toAbsolutePath().normalize().toString()))
                .setDashboardProvider(dashboardProvider)
                .addPlugin(RevealAIPlugin.withOptions(aiPluginOptions))
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
