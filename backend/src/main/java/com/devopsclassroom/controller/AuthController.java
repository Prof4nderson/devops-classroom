package com.devopsclassroom.controller;

import com.devopsclassroom.dto.AuthRequest;
import com.devopsclassroom.dto.AuthResponse;
import com.devopsclassroom.dto.UsuarioRequest;
import com.devopsclassroom.dto.UsuarioResponse;
import com.devopsclassroom.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = usuarioService.autenticar(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.criarUsuario(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(@PathVariable Long id,
                                                             @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(id, request));
    }

    @GetMapping("/users/alunos/search")
    public ResponseEntity<List<UsuarioResponse>> buscarAlunos(@RequestParam String nome) {
        return ResponseEntity.ok(usuarioService.buscarAlunos(nome));
    }

    @GetMapping("/users/professores")
    public ResponseEntity<List<UsuarioResponse>> buscarProfessores() {
        return ResponseEntity.ok(usuarioService.buscarProfessores());
    }
}
