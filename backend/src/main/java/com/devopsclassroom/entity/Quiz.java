package com.devopsclassroom.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Usuario professor;

    @Column(nullable = false, length = 500)
    private String pergunta;

    @Column(length = 1000)
    private String opcoes; // JSON array of strings

    @Column(name = "resposta_correta", length = 200)
    private String respostaCorreta;

    @Column(name = "tempo_limite_segundos")
    private Integer tempoLimiteSegundos;

    @Column(name = "status_quiz")
    @Enumerated(EnumType.STRING)
    private StatusQuiz status;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<RespostaQuiz> respostas;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        if (status == null) status = StatusQuiz.ATIVO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }
    public Usuario getProfessor() { return professor; }
    public void setProfessor(Usuario professor) { this.professor = professor; }
    public String getPergunta() { return pergunta; }
    public void setPergunta(String pergunta) { this.pergunta = pergunta; }
    public String getOpcoes() { return opcoes; }
    public void setOpcoes(String opcoes) { this.opcoes = opcoes; }
    public String getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(String respostaCorreta) { this.respostaCorreta = respostaCorreta; }
    public Integer getTempoLimiteSegundos() { return tempoLimiteSegundos; }
    public void setTempoLimiteSegundos(Integer tempoLimiteSegundos) { this.tempoLimiteSegundos = tempoLimiteSegundos; }
    public StatusQuiz getStatus() { return status; }
    public void setStatus(StatusQuiz status) { this.status = status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getFinalizadoEm() { return finalizadoEm; }
    public void setFinalizadoEm(LocalDateTime finalizadoEm) { this.finalizadoEm = finalizadoEm; }
    public List<RespostaQuiz> getRespostas() { return respostas; }
    public void setRespostas(List<RespostaQuiz> respostas) { this.respostas = respostas; }
}
