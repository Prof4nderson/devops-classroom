package com.devopsclassroom.config;

import com.devopsclassroom.entity.Usuario;
import com.devopsclassroom.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtConfig jwtConfig, UsuarioRepository usuarioRepository) {
        this.jwtConfig = jwtConfig;
        this.usuarioRepository = usuarioRepository;
    }

    // 💡 Evita reprocessar ou travar chamadas de Login/Cadastro/WebSocket
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") || path.startsWith("/ws/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtConfig.validateToken(token)) {
                String login = jwtConfig.getLoginFromToken(token);
                Long userId = jwtConfig.getUserIdFromToken(token);
                String tipoUsuario = jwtConfig.getTipoUsuarioFromToken(token);

                Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    usuario, null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + tipoUsuario))
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}