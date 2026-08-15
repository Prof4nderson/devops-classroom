package com.devopsclassroom.controller;

import com.devopsclassroom.dto.MensagemRequest;
import com.devopsclassroom.entity.Mensagem;
import com.devopsclassroom.entity.TipoUsuario;
import com.devopsclassroom.entity.Usuario;
import com.devopsclassroom.service.AIAgentService;
import com.devopsclassroom.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.devopsclassroom.repository.UsuarioRepository;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    private final MensagemService mensagemService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UsuarioRepository usuarioRepository; // 1. Declarar aqui
    private final AIAgentService aiAgentService;

    // 2. Injetar no construtor
    public MensagemController(MensagemService mensagemService,
                              SimpMessagingTemplate messagingTemplate,
                              UsuarioRepository usuarioRepository,
                              AIAgentService aiAgentService) {
        this.mensagemService = mensagemService;
        this.messagingTemplate = messagingTemplate;
        this.usuarioRepository = usuarioRepository;
        this.aiAgentService = aiAgentService;
    }

    // ... seus métodos HTTP (enviarMensagem, listarMensagensDaAula) ...

    @MessageMapping("/chat/{aulaId}/send")
    public void receberMensagemWebSocket(@DestinationVariable Long aulaId,
                                         MensagemRequest request,
                                         Principal principal) {
        if (principal != null) {
            var usuarioOpt = usuarioRepository.findByLogin(principal.getName());

            if (usuarioOpt.isPresent()) {
                Long usuarioId = usuarioOpt.get().getId();

                Mensagem mensagemSalva = mensagemService.criarMensagem(request, usuarioId);
                messagingTemplate.convertAndSend("/topic/chat/" + aulaId, mensagemSalva);

                // 2. 🤖 GATILHO DO @CODER: Se a mensagem contiver "@Coder"
                if (request.getConteudo() != null && request.getConteudo().contains("@Coder")) {
                    String perguntaParaIA = request.getConteudo().replace("@Coder", "").trim();

                    // Pede a resposta para o Ollama via AIAgentService
                    String respostaIA = aiAgentService.responder(perguntaParaIA);

                    // Busca o usuário do bot pelo login ou cria um automático se não existir
                    Usuario usuarioAi = usuarioRepository.findByLogin("coder")
                            .orElseGet(() -> {
                                Usuario novoBot = new Usuario();
                                novoBot.setLogin("coder");
                                novoBot.setNome("Coder");
                                novoBot.setTipo(TipoUsuario.AI);
                                novoBot.setSenha("123456");
                                return usuarioRepository.save(novoBot);
                            });

                    // Salva e transmite a mensagem da IA vinculada ao usuário correto
                    MensagemRequest reqBot = new MensagemRequest();
                    reqBot.setConteudo(respostaIA);
                    reqBot.setAulaId(aulaId);

                    Mensagem mensagemBot = mensagemService.criarMensagem(reqBot, usuarioAi.getId());
                    messagingTemplate.convertAndSend("/topic/chat/" + aulaId, mensagemBot);
                }
            }
        }
    }
}