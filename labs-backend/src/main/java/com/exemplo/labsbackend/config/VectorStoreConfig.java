package com.exemplo.labsbackend.config;


import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Bean
    public EmbeddingStore embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("ai_db")
                .user("postgres")
                .password("123456")
                .table("documents")
                .dimension(1536) // Compatível com o modelo 'text-embedding-3-small' ou 'ada-002'
                .build();
    }
}