package com.revealbi.samples.chat;

import com.revealbi.samples.chat.reveal.DataSourceProvider;
import io.revealbi.ai.RevealAIPlugin;
import io.revealbi.ai.RevealAIPluginOptions;
import io.revealbi.core.IRevealServer;
import io.revealbi.core.RevealServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
    public IRevealServer revealServer(DataSourceProvider dataSourceProvider) {
        // AI provider settings – replace with your own API key or load from env
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null) apiKey = "YOUR_OPENAI_API_KEY";

        Map<String, Object> aiSettings = Map.of(
                "DefaultClient", "openai",
                "OpenAI", Map.of("ApiKey", apiKey, "ModelId", "gpt-4.1")
        );

        RevealAIPluginOptions aiPluginOptions = new RevealAIPluginOptions(
                Path.of("src", "main", "resources", "Reveal", "Metadata", "catalog.json")
                        .toAbsolutePath().normalize().toString(),
                new RevealAIPluginOptions.MetadataManagerOptions(
                        Path.of(System.getProperty("user.home"), "AImetadata").toString()),
                null,
                Map.of("settings", aiSettings));

        return new RevealServerBuilder()
                .setDataSourceProvider(dataSourceProvider)
                .addPlugin(RevealAIPlugin.withOptions(aiPluginOptions))
                .build();
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
