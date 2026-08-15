package com.devopsclassroom.dto;

import jakarta.validation.constraints.*;

public class AIChatRequest {
    @NotNull(message = "ID da aula é obrigatório")
    private Long aulaId;

    @NotBlank(message = "Mensagem é obrigatória")
    private String mensagem;

    public Long getAulaId() { return aulaId; }
    public void setAulaId(Long aulaId) { this.aulaId = aulaId; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
