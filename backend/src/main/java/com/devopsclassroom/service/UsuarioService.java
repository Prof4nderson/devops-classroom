package com.devopsclassroom.service;

import com.devopsclassroom.config.JwtConfig;
import com.devopsclassroom.dto.*;
import com.devopsclassroom.entity.Usuario;
import com.devopsclassroom.entity.TipoUsuario;
import com.devopsclassroom.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtConfig jwtConfig) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig = jwtConfig;
    }

    @Transactional
    public AuthResponse autenticar(AuthRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(request.getLogin());
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }
        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }
        String token = jwtConfig.generateToken(usuario.getLogin(), usuario.getId(), usuario.getTipo().name());
        return new AuthResponse(token, usuario.getId(), usuario.getNome(), usuario.getLogin(), usuario.getTipo().name());
    }

    @Transactional
    public UsuarioResponse criarUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("Login já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(request.getLogin());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());
        usuario.setTipo(TipoUsuario.valueOf(request.getTipo().toUpperCase()));
        usuario.setInstituicao(request.getInstituicao());
        usuario.setBio(request.getBio());

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioResponse atualizarUsuario(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (request.getNome() != null) usuario.setNome(request.getNome());
        if (request.getEmail() != null) usuario.setEmail(request.getEmail());
        if (request.getTelefone() != null) usuario.setTelefone(request.getTelefone());
        if (request.getInstituicao() != null) usuario.setInstituicao(request.getInstituicao());
        if (request.getBio() != null) usuario.setBio(request.getBio());
        if (request.getSenha() != null) usuario.setSenha(passwordEncoder.encode(request.getSenha()));

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }

    public List<UsuarioResponse> buscarAlunos(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome).stream()
                .filter(u -> u.getTipo() == TipoUsuario.ALUNO)
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    public List<UsuarioResponse> buscarProfessores() {
        return usuarioRepository.findByTipo(TipoUsuario.PROFESSOR).stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }
}
