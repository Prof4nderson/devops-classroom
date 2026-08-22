package com.exemplo.labsbackend.controller;



import com.exemplo.labsbackend.service.AiService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody PromptRequest request) {
        String response = aiService.generateResponse(request.getPrompt());
        return ResponseEntity.ok(response);
    }

    @Data
    public static class PromptRequest {
        private String prompt;
    }
}