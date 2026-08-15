package com.devopsclassroom.repository;

import com.devopsclassroom.entity.Aula;
import com.devopsclassroom.entity.StatusAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByCursoId(Long cursoId);
    List<Aula> findByStatus(StatusAula status);
}
