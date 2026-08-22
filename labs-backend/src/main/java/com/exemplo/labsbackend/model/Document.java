package com.exemplo.labsbackend.model;


import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documents")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String metadata;

    @Column(columnDefinition = "vector")
    private PGvector embedding;
}