package com.devopsclassroom.dto;

import jakarta.validation.constraints.*;

public class AulaRequest {
    @NotNull(message = "ID do curso é obrigatório")
    private Long cursoId;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;
    private String dataAula; // ISO format
    private String duracao;
    private String status;

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDataAula() { return dataAula; }
    public void setDataAula(String dataAula) { this.dataAula = dataAula; }
    public String getDuracao() { return duracao; }
    public void setDuracao(String duracao) { this.duracao = duracao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
