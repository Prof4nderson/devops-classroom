package com.devopsclassroom.repository;

import com.devopsclassroom.entity.RespostaQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RespostaQuizRepository extends JpaRepository<RespostaQuiz, Long> {
    List<RespostaQuiz> findByQuizId(Long quizId);
    Optional<RespostaQuiz> findByQuizIdAndUsuarioId(Long quizId, Long usuarioId);
    boolean existsByQuizIdAndUsuarioId(Long quizId, Long usuarioId);
}
