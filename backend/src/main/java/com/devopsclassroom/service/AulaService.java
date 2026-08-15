package com.devopsclassroom.service;

import com.devopsclassroom.dto.AulaRequest;
import com.devopsclassroom.entity.Aula;
import com.devopsclassroom.entity.Curso;
import com.devopsclassroom.entity.StatusAula;
import com.devopsclassroom.repository.AulaRepository;
import com.devopsclassroom.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final CursoRepository cursoRepository;

    public AulaService(AulaRepository aulaRepository, CursoRepository cursoRepository) {
        this.aulaRepository = aulaRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public Aula criarAula(AulaRequest request) {
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        Aula aula = new Aula();
        aula.setCurso(curso);
        aula.setTitulo(request.getTitulo());
        aula.setDescricao(request.getDescricao());
        if (request.getDataAula() != null) {
            aula.setDataAula(LocalDateTime.parse(request.getDataAula()));
        }
        aula.setDuracao(request.getDuracao());
        if (request.getStatus() != null) {
            aula.setStatus(StatusAula.valueOf(request.getStatus().toUpperCase()));
        }

        return aulaRepository.save(aula);
    }

    public List<Aula> listarAulasPorCurso(Long cursoId) {
        return aulaRepository.findByCursoId(cursoId);
    }

    public Aula buscarAula(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula não encontrada"));
    }

    @Transactional
    public Aula iniciarAula(Long aulaId) {
        Aula aula = buscarAula(aulaId);
        aula.setStatus(StatusAula.EM_ANDAMENTO);
        return aulaRepository.save(aula);
    }

    @Transactional
    public Aula finalizarAula(Long aulaId) {
        Aula aula = buscarAula(aulaId);
        aula.setStatus(StatusAula.FINALIZADA);
        return aulaRepository.save(aula);
    }

    public List<Aula> listarAulasEmAndamento() {
        return aulaRepository.findByStatus(StatusAula.EM_ANDAMENTO);
    }
}
