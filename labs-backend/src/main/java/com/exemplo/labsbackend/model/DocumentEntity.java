package com.exemplo.labsbackend.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    // O vetor de embedding que o pgvector vai pesquisar
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "vector(1536)") // Tamanho para modelos da OpenAI
    private float[] embedding;
}