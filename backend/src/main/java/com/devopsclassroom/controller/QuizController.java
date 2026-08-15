package com.devopsclassroom.controller;

import com.devopsclassroom.dto.QuizRequest;
import com.devopsclassroom.dto.RespostaQuizRequest;
import com.devopsclassroom.entity.Quiz;
import com.devopsclassroom.entity.RespostaQuiz;
import com.devopsclassroom.entity.Usuario;
import com.devopsclassroom.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<Quiz> criarQuiz(@Valid @RequestBody QuizRequest request, Authentication auth) {
        Usuario professor = (Usuario) auth.getPrincipal();
        Quiz quiz = quizService.criarQuiz(request, professor.getId());
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/responder")
    public ResponseEntity<RespostaQuiz> responderQuiz(@Valid @RequestBody RespostaQuizRequest request,
                                                       Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        RespostaQuiz resposta = quizService.responderQuiz(request, usuario.getId());
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Quiz> finalizarQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.finalizarQuiz(id));
    }

    @GetMapping("/aula/{aulaId}")
    public ResponseEntity<List<Quiz>> listarQuizzesDaAula(@PathVariable Long aulaId) {
        return ResponseEntity.ok(quizService.listarQuizzesDaAula(aulaId));
    }

    @GetMapping("/{id}/respostas")
    public ResponseEntity<List<RespostaQuiz>> listarRespostas(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.listarRespostasDoQuiz(id));
    }
}
