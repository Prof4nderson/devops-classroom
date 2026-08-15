package com.devopsclassroom.service;

import com.devopsclassroom.dto.CursoRequest;
import com.devopsclassroom.dto.MatriculaRequest;
import com.devopsclassroom.entity.Curso;
import com.devopsclassroom.entity.Usuario;
import com.devopsclassroom.entity.Matricula;
import com.devopsclassroom.repository.CursoRepository;
import com.devopsclassroom.repository.MatriculaRepository;
import com.devopsclassroom.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;

    public CursoService(CursoRepository cursoRepository, MatriculaRepository matriculaRepository,
                       UsuarioRepository usuarioRepository) {
        this.cursoRepository = cursoRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Curso criarCurso(CursoRequest request, Usuario professor) {
        Curso curso = new Curso();
        curso.setNome(request.getNome());
        curso.setDescricao(request.getDescricao());
        curso.setCodigo(request.getCodigo());
        curso.setProfessor(professor);
        return cursoRepository.save(curso);
    }

    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    public Curso buscarCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
    }

    @Transactional
    public void matricularAluno(MatriculaRequest request) {
        Usuario aluno = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        if (matriculaRepository.existsByUsuarioIdAndCursoId(request.getUsuarioId(), request.getCursoId())) {
            throw new RuntimeException("Aluno já matriculado neste curso");
        }

        Matricula matricula = new Matricula();
        matricula.setUsuario(aluno);
        matricula.setCurso(curso);
        matriculaRepository.save(matricula);
    }

    public List<Usuario> listarAlunosDoCurso(Long cursoId) {
        return matriculaRepository.findByCursoId(cursoId).stream()
                .map(Matricula::getUsuario)
                .toList();
    }

    public List<Curso> listarCursosDoProfessor(Long professorId) {
        return cursoRepository.findByProfessorId(professorId);
    }
}
