package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MenuImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuImageService {
    List<MenuImage> createBulkImages(List<MultipartFile> files, Menu menu);
}
