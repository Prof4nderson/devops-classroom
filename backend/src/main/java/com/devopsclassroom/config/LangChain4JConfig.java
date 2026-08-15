package com.devopsclassroom.config;

import com.devopsclassroom.ai.CoderAssistant;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChain4JConfig {

    @Value("${langchain4j.ollama.base-url}")
    private String baseUrl;

    @Value("${langchain4j.ollama.model-name}")
    private String modelName;

    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(120))
                .build();
    }

    @Bean
    public OllamaStreamingChatModel ollamaStreamingChatModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(120))
                .build();
    }

    @Bean
    public CoderAssistant coderAssistant(OllamaChatModel chatModel) {
        return dev.langchain4j.service.AiServices.builder(CoderAssistant.class)
                .chatLanguageModel(chatModel)
                .build();
    }
}
