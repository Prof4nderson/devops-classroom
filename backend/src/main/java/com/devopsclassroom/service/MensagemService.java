package com.devopsclassroom.service;

import com.devopsclassroom.dto.MensagemRequest;
import com.devopsclassroom.entity.*;
import com.devopsclassroom.repository.AulaRepository;
import com.devopsclassroom.repository.MensagemRepository;
import com.devopsclassroom.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MensagemService {

    private final MensagemRepository mensagemRepository;
    private final AulaRepository aulaRepository;
    private final UsuarioRepository usuarioRepository;

    public MensagemService(MensagemRepository mensagemRepository, AulaRepository aulaRepository,
                          UsuarioRepository usuarioRepository) {
        this.mensagemRepository = mensagemRepository;
        this.aulaRepository = aulaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Mensagem criarMensagem(MensagemRequest request, Long usuarioId) {
        Aula aula = aulaRepository.findById(request.getAulaId())
                .orElseThrow(() -> new RuntimeException("Aula não encontrada"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Mensagem mensagem = new Mensagem();
        mensagem.setAula(aula);
        mensagem.setUsuario(usuario);
        mensagem.setConteudo(request.getConteudo());
        if (request.getTipo() != null) {
            mensagem.setTipo(TipoMensagem.valueOf(request.getTipo().toUpperCase()));
        }
        mensagem.setUrlMidia(request.getUrlMidia());
        mensagem.setNomeArquivo(request.getNomeArquivo());
        mensagem.setMimeType(request.getMimeType());

        return mensagemRepository.save(mensagem);
    }

    public List<Mensagem> listarMensagensDaAula(Long aulaId) {
        return mensagemRepository.findByAulaIdOrderByCriadoEmAsc(aulaId);
    }
}
