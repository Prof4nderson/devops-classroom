package com.devopsclassroom.config;

import com.devopsclassroom.repository.UsuarioRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final UsuarioRepository usuarioRepository;
    private final JwtConfig jwtConfig;

    // 🎯 Injeção via construtor (evita conflitos de ciclo de vida no Spring)
    public WebSocketConfig(UsuarioRepository usuarioRepository, JwtConfig jwtConfig) {
        this.usuarioRepository = usuarioRepository;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = null;

                    // 1. Tenta pegar do header nativo (case-insensitive)
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader == null) {
                        authHeader = accessor.getFirstNativeHeader("authorization");
                    }

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                    } else {
                        // 2. Se não achou no header, tenta pegar dos parâmetros da URL (Query Param)
                        var nativeHeaders = accessor.getNativeHeader("token");
                        if (nativeHeaders != null && !nativeHeaders.isEmpty()) {
                            token = nativeHeaders.get(0);
                        } else if (accessor.getSessionAttributes() != null && accessor.getSessionAttributes().containsKey("token")) {
                            token = (String) accessor.getSessionAttributes().get("token");
                        }
                    }

                    if (token != null) {
                        try {
                            if (jwtConfig.validateToken(token)) {
                                String username = jwtConfig.getLoginFromToken(token);

                                usuarioRepository.findByLogin(username)
                                        .or(() -> usuarioRepository.findByEmail(username))
                                        .ifPresent(usuario -> {
                                            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name()));
                                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                                    usuario.getLogin(), null, authorities
                                            );
                                            accessor.setUser(auth);
                                        });
                            }
                        } catch (Exception e) {
                            System.err.println("Erro ao autenticar conexão WebSocket:");
                            e.printStackTrace();
                        }
                    }
                }
                return message;
            }
        });
    }
}