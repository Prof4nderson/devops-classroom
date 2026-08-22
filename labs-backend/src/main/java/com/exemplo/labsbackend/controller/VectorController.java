package com.exemplo.labsbackend.controller;



import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vector")
@RequiredArgsConstructor
public class VectorController {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    @PostMapping("/index")
    public String index(@RequestBody String text) {
        // Gera o vetor (embedding) do texto
        Embedding embedding = embeddingModel.embed(text).content();
        // Salva no Postgres
        embeddingStore.add(embedding, TextSegment.from(text));
        return "Documento indexado com sucesso!";
    }
}