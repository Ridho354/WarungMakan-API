package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MenuImage;
import com.enigma.wmb_api.repository.MenuImageRepository;
import com.enigma.wmb_api.service.MenuImageService;
import com.enigma.wmb_api.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuImageServiceImpl implements MenuImageService {
    private final MenuImageRepository menuImageRepository;
    private final StorageService storageService;

    @Override
    public List<MenuImage> createBulkImages(List<MultipartFile> multipartFiles, Menu menu
    ) {
        List<MenuImage> menuImages = new ArrayList<>();
        multipartFiles.forEach(multipartFileItem -> {
            Map cloudinaryResp = storageService.uploadFile(multipartFileItem, "menu");
            MenuImage menuImage = MenuImage.builder()
                    .publicId((String) cloudinaryResp.get("public_id"))
                    .secureUrl((String) cloudinaryResp.get("secure_url"))
                    .menu(menu)
                    .build();
            menuImages.add(menuImage);
        });

        menuImageRepository.saveAllAndFlush(menuImages);

        return menuImages;
    }
}
