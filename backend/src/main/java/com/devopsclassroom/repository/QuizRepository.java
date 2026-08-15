package com.devopsclassroom.repository;

import com.devopsclassroom.entity.Quiz;
import com.devopsclassroom.entity.StatusQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByAulaId(Long aulaId);
    Optional<Quiz> findByIdAndStatus(Long id, StatusQuiz status);
    List<Quiz> findByStatus(StatusQuiz status);
}
