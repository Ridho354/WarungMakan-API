package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@ConditionalOnProperty(name = "storage.service.type", havingValue = "LOCAL")
public class LocalStorageServiceImpl implements StorageService {
    private final Path rootPath;

    private final Long maxFileSize;

    public LocalStorageServiceImpl(
            @Value("${storage.local.root-path}") Path rootPath,
            @Value("${storage.local.max-file-size}") Long maxFileSize
    ) {
        this.rootPath = rootPath;
        this.maxFileSize = maxFileSize;
    }

    @PostConstruct
    public void initialize() throws IOException {
        Files.createDirectories(rootPath);
    }

    @Override
    public Map<String, String> uploadFile(MultipartFile file, String folder) {
        try {
            validateFile(file);
            // normalize untuk memastikan path yang consistent antara OS
            Path folderPath = rootPath.resolve(folder).normalize();

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = rootPath.resolve(folderPath).resolve(filename);

            Files.copy(file.getInputStream(), filePath);

            Map<String, String> result = new HashMap<>();
            result.put("public_id", folder + "/" + filename);
            result.put("secure_url", "api/v1/files" + folder + "/" + filename);
            return result;
        } catch (Exception e) {
            log.error("LocalStorageServiceImpl-uploadFile: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file");
        }
    }

    @Override
    public void deleteFile(String identifier) {
        try {
            Path filePath = rootPath.resolve(identifier).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file");
        }
    }

    @Override
    public Resource downloadFile(String identifier) {
       try {
           Path filePath = rootPath.resolve(identifier).normalize();
           Resource resource = new UrlResource(filePath.toUri());

           if (resource.exists()) {
               return resource;
           } else {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
           }
       } catch (IOException e) {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file");
       }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (file.getSize() > maxFileSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is too large");
        }
    }
}
