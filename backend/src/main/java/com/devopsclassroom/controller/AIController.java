package com.devopsclassroom.controller;

import com.devopsclassroom.dto.AIChatRequest;
import com.devopsclassroom.service.AIAgentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIAgentService aiAgentService;

    public AIController(AIAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @PostMapping("/coder")
    public ResponseEntity<Map<String, String>> chamarCoder(@Valid @RequestBody AIChatRequest request) {
        String resposta = aiAgentService.responder(request.getMensagem());
        return ResponseEntity.ok(Map.of(
                "resposta", resposta,
                "assistente", "Coder"
        ));
    }
}
