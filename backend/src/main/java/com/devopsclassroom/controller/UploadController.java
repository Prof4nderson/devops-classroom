package com.devopsclassroom.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    @Value("${upload.directory}")
    private String uploadDirectory;

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return processUpload(file);
    }

    @PostMapping("/file")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return processUpload(file);
    }

    private ResponseEntity<Map<String, String>> processUpload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        // Criar diretório se não existir
        File dir = new File(uploadDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Gerar nome único
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Salvar arquivo
        File dest = new File(uploadDirectory + fileName);
        file.transferTo(dest);

        String url = "/uploads/" + fileName;

        return ResponseEntity.ok(Map.of(
                "url", url,
                "nomeArquivo", originalName,
                "mimeType", file.getContentType(),
                "size", String.valueOf(file.getSize())
        ));
    }
}
