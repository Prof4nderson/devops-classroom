package com.devopsclassroom.dto;

import jakarta.validation.constraints.*;

public class CursoRequest {
    @NotBlank(message = "Nome do curso é obrigatório")
    private String nome;

    private String descricao;
    private String codigo;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
