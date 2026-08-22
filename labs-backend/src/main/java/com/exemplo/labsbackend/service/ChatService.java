package com.exemplo.labsbackend.service;


import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final Assistant assistant;

    public ChatService(ChatLanguageModel chatLanguageModel,
                       EmbeddingStore<TextSegment> embeddingStore,
                       EmbeddingModel embeddingModel) {

        // Configura o retriever (busca semântica no Postgres)
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3) // Busca os 3 documentos mais parecidos
                .build();

        // Cria o assistente com memória de conversa (10 mensagens)
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    public String chat(String userId, String message) {
        return assistant.chat(userId, message);
    }

    // Interface que o LangChain4j usa para implementar a lógica
    public interface Assistant {
        String chat(Object memoryId, String message);
    }
}