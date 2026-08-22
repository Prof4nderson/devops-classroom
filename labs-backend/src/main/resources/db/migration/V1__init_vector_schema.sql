-- Habilita a extensão pgvector no PostgreSQL
CREATE EXTENSION IF NOT EXISTS vector;

-- Tabela de documentos ou base de conhecimento para RAG
CREATE TABLE documents (
                           id BIGSERIAL PRIMARY KEY,
                           content TEXT NOT NULL,
                           metadata VARCHAR(255),
    -- Definindo o vetor com dimensão 1536 (compatível com modelos text-embedding-3-small da OpenAI)
                           embedding VECTOR(1536)
);

-- Tabela de usuários para o Spring Security / JWT
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);