package com.devopsclassroom.service;

import com.devopsclassroom.dto.QuizRequest;
import com.devopsclassroom.dto.RespostaQuizRequest;
import com.devopsclassroom.entity.*;
import com.devopsclassroom.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final RespostaQuizRepository respostaQuizRepository;
    private final AulaRepository aulaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    public QuizService(QuizRepository quizRepository, RespostaQuizRepository respostaQuizRepository,
                      AulaRepository aulaRepository, UsuarioRepository usuarioRepository,
                      ObjectMapper objectMapper) {
        this.quizRepository = quizRepository;
        this.respostaQuizRepository = respostaQuizRepository;
        this.aulaRepository = aulaRepository;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Quiz criarQuiz(QuizRequest request, Long professorId) {
        Aula aula = aulaRepository.findById(request.getAulaId())
                .orElseThrow(() -> new RuntimeException("Aula não encontrada"));
        Usuario professor = usuarioRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Quiz quiz = new Quiz();
        quiz.setAula(aula);
        quiz.setProfessor(professor);
        quiz.setPergunta(request.getPergunta());
        try {
            quiz.setOpcoes(objectMapper.writeValueAsString(request.getOpcoes()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar opções");
        }
        quiz.setRespostaCorreta(request.getRespostaCorreta());
        quiz.setTempoLimiteSegundos(request.getTempoLimiteSegundos());

        return quizRepository.save(quiz);
    }

    @Transactional
    public RespostaQuiz responderQuiz(RespostaQuizRequest request, Long usuarioId) {
        Quiz quiz = quizRepository.findByIdAndStatus(request.getQuizId(), StatusQuiz.ATIVO)
                .orElseThrow(() -> new RuntimeException("Quiz não encontrado ou já finalizado"));

        if (respostaQuizRepository.existsByQuizIdAndUsuarioId(request.getQuizId(), usuarioId)) {
            throw new RuntimeException("Você já respondeu este quiz");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        RespostaQuiz resposta = new RespostaQuiz();
        resposta.setQuiz(quiz);
        resposta.setUsuario(usuario);
        resposta.setRespostaSelecionada(request.getRespostaSelecionada());
        boolean correta = request.getRespostaSelecionada().equals(quiz.getRespostaCorreta());
        resposta.setEstaCorreta(correta);

        return respostaQuizRepository.save(resposta);
    }

    @Transactional
    public Quiz finalizarQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz não encontrado"));
        quiz.setStatus(StatusQuiz.FINALIZADO);
        quiz.setFinalizadoEm(LocalDateTime.now());
        return quizRepository.save(quiz);
    }

    public List<Quiz> listarQuizzesDaAula(Long aulaId) {
        return quizRepository.findByAulaId(aulaId);
    }

    public List<RespostaQuiz> listarRespostasDoQuiz(Long quizId) {
        return respostaQuizRepository.findByQuizId(quizId);
    }
}
