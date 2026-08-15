package com.devopsclassroom.dto;

import jakarta.validation.constraints.*;

public class RespostaQuizRequest {
    @NotNull(message = "ID do quiz é obrigatório")
    private Long quizId;

    @NotBlank(message = "Resposta é obrigatória")
    private String respostaSelecionada;

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public String getRespostaSelecionada() { return respostaSelecionada; }
    public void setRespostaSelecionada(String respostaSelecionada) { this.respostaSelecionada = respostaSelecionada; }
}
