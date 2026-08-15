package com.devopsclassroom.dto;

import jakarta.validation.constraints.*;

public class MensagemRequest {
    @NotNull(message = "ID da aula é obrigatório")
    private Long aulaId;

    private String conteudo;
    private String tipo; // TEXT, IMAGE, CODE, SCREENSHOT, FILE
    private String urlMidia;
    private String nomeArquivo;
    private String mimeType;

    public Long getAulaId() { return aulaId; }
    public void setAulaId(Long aulaId) { this.aulaId = aulaId; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUrlMidia() { return urlMidia; }
    public void setUrlMidia(String urlMidia) { this.urlMidia = urlMidia; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
}
