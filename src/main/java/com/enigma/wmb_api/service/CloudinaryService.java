package com.enigma.wmb_api.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    Map upload(MultipartFile file, String folderName);
    void delete(String publicId);
}
