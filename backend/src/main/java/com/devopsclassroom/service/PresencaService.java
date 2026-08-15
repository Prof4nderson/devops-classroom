package com.devopsclassroom.service;

import com.devopsclassroom.entity.Aula;
import com.devopsclassroom.entity.Presenca;
import com.devopsclassroom.entity.Usuario;
import com.devopsclassroom.repository.AulaRepository;
import com.devopsclassroom.repository.PresencaRepository;
import com.devopsclassroom.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;
    private final AulaRepository aulaRepository;
    private final UsuarioRepository usuarioRepository;

    public PresencaService(PresencaRepository presencaRepository, AulaRepository aulaRepository,
                          UsuarioRepository usuarioRepository) {
        this.presencaRepository = presencaRepository;
        this.aulaRepository = aulaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Presenca registrarPresenca(Long usuarioId, Long aulaId) {
        if (presencaRepository.existsByUsuarioIdAndAulaId(usuarioId, aulaId)) {
            throw new RuntimeException("Presença já registrada");
        }

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RuntimeException("Aula não encontrada"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Presenca presenca = new Presenca();
        presenca.setUsuario(usuario);
        presenca.setAula(aula);
        return presencaRepository.save(presenca);
    }

    public List<Usuario> listarPresentes(Long aulaId) {
        return presencaRepository.findByAulaId(aulaId).stream()
                .map(Presenca::getUsuario)
                .toList();
    }

    public long contarPresentes(Long aulaId) {
        return presencaRepository.findByAulaId(aulaId).size();
    }

    public boolean verificarPresenca(Long usuarioId, Long aulaId) {
        return presencaRepository.existsByUsuarioIdAndAulaId(usuarioId, aulaId);
    }
}
