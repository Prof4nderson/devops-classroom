package com.devopsclassroom.service;

import com.devopsclassroom.ai.CoderAssistant;
import dev.langchain4j.service.TokenStream;
import org.springframework.stereotype.Service;

@Service
public class AIAgentService {

    private final CoderAssistant coderAssistant;

    public AIAgentService(CoderAssistant coderAssistant) {
        this.coderAssistant = coderAssistant;
    }

    public String responder(String pergunta) {
        try {
            return coderAssistant.chatSync(pergunta);
        } catch (Exception e) {
            return "Erro ao conectar com o assistente Coder. Verifique se o Ollama está rodando localmente (http://localhost:11434). " +
                   "Detalhe: " + e.getMessage();
        }
    }

    public TokenStream responderStream(String pergunta) {
        return coderAssistant.chat(pergunta);
    }
}
