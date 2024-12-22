package com.enigma.wmb_api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface StorageService {
    Map<String, String> uploadFile(MultipartFile file, String folder);
    void deleteFile(String identifier);
    Resource downloadFile(String identifier);
}
