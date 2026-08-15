package com.devopsclassroom.ai;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

//@AiService
public interface CoderAssistant {

    @SystemMessage("""
        Você é o @Coder, um assistente de IA especializado em Java, Spring Boot, Spring Security, Docker e DevOps.
        Responda SEMPRE de forma direta, técnica, educada e extremamente concisa.
        Quando relevante, forneça exemplos de código ou comandos. Proibido fazer divagações filosóficas.
        """)
    String chatSync(@UserMessage String userMessage);

    @SystemMessage("""
        Você é o @Coder, um assistente de IA especializado em Java, Spring Boot, Spring Security, Docker e DevOps.
        Responda SEMPRE de forma direta, técnica, educada e extremamente concisa.
        Quando relevante, forneça exemplos de código ou comandos. Proibido fazer divagações filosóficas.
        """)
    TokenStream chat(@UserMessage String message);
}