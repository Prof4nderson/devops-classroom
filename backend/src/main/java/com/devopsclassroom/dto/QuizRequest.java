package com.devopsclassroom.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class QuizRequest {
    @NotNull(message = "ID da aula é obrigatório")
    private Long aulaId;

    @NotBlank(message = "Pergunta é obrigatória")
    private String pergunta;

    @NotNull(message = "Opções são obrigatórias")
    @Size(min = 2, max = 6)
    private List<String> opcoes;

    @NotBlank(message = "Resposta correta é obrigatória")
    private String respostaCorreta;

    private Integer tempoLimiteSegundos;

    // Getters and Setters
    public Long getAulaId() { return aulaId; }
    public void setAulaId(Long aulaId) { this.aulaId = aulaId; }
    public String getPergunta() { return pergunta; }
    public void setPergunta(String pergunta) { this.pergunta = pergunta; }
    public List<String> getOpcoes() { return opcoes; }
    public void setOpcoes(List<String> opcoes) { this.opcoes = opcoes; }
    public String getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(String respostaCorreta) { this.respostaCorreta = respostaCorreta; }
    public Integer getTempoLimiteSegundos() { return tempoLimiteSegundos; }
    public void setTempoLimiteSegundos(Integer tempoLimiteSegundos) { this.tempoLimiteSegundos = tempoLimiteSegundos; }
}
