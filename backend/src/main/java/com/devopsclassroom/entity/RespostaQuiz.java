package com.devopsclassroom.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "respostas_quiz")
public class RespostaQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 500)
    private String respostaSelecionada;

    @Column(name = "esta_correta")
    private Boolean estaCorreta;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @PrePersist
    protected void onCreate() {
        dataResposta = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getRespostaSelecionada() { return respostaSelecionada; }
    public void setRespostaSelecionada(String respostaSelecionada) { this.respostaSelecionada = respostaSelecionada; }
    public Boolean getEstaCorreta() { return estaCorreta; }
    public void setEstaCorreta(Boolean estaCorreta) { this.estaCorreta = estaCorreta; }
    public LocalDateTime getDataResposta() { return dataResposta; }
    public void setDataResposta(LocalDateTime dataResposta) { this.dataResposta = dataResposta; }
}
