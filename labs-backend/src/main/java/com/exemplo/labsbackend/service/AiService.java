package com.exemplo.labsbackend.service;


import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatLanguageModel chatLanguageModel;

    public String generateResponse(String prompt) {
        return chatLanguageModel.generate(prompt);
    }
}
