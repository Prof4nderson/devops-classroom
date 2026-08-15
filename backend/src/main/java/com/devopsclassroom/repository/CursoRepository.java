package com.devopsclassroom.repository;

import com.devopsclassroom.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    Optional<Curso> findByCodigo(String codigo);
    List<Curso> findByProfessorId(Long professorId);
    List<Curso> findByNomeContainingIgnoreCase(String nome);
}
