package com.exemplo.labsbackend.repository;

import com.exemplo.labsbackend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query(value = "SELECT * FROM documents ORDER BY embedding <=> CAST(:queryEmbedding AS vector) LIMIT :matchCount", nativeQuery = true)
    List<Document> findSimilarDocuments(@Param("queryEmbedding") String queryEmbedding, @Param("matchCount") int matchCount);
}