package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.MenuCategory;
import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MenuImage;
import com.enigma.wmb_api.repository.MenuRepository;
import com.enigma.wmb_api.service.MenuImageService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.service.StorageService;
import com.enigma.wmb_api.specification.MenuSpecification;
import com.enigma.wmb_api.util.SortUtil;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

//@AllArgsConstructor
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final MenuImageService menuImageService;
    private final StorageService storageService;
    private final ValidationUtil validationUtil;

    @Override
    public MenuResponse createMenu(MenuRequest menuRequest, List<MultipartFile> multipartFiles) {
        // #SPRING VALIDATION# dipanggil
//        validationUtil.validate(menuRequest); jika tidak pakai @Validated dan @Valid
        Menu menu = Menu.builder()
                .name(menuRequest.getName())
                .price(menuRequest.getPrice())
                .category(MenuCategory.fromValue(menuRequest.getCategory()))
                .isAvailable(true)
                .stock(menuRequest.getStock())
                .build();
        Menu createdMenu = menuRepository.saveAndFlush(menu);

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            List<MenuImage> menuImageList = menuImageService.createBulkImages(multipartFiles, createdMenu); // disini membuat MenuImage (save ke database)
            menu.setImages(menuImageList); // ini untuk keperluan response
        }

        return toMenuResponse(menu);
    }

    @Override
    public MenuResponse getMenuById(String id) {
        Menu menu = getOne(id);
        return toMenuResponse(menu);
    }

    @Override
    public Menu getOne(String id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.ERROR_MENU_NOT_FOUND));
    }

//    @Override
//    public List<MenuResponse> getAll(Integer name, Long price, String menuCategory) {
//        List<MenuResponse> menuResponses = new ArrayList<>();
//        if (name != null && price != null) {
//            menuRepository.findByNameIgnoreCaseAndPrice(name, price).forEach(menu -> menuResponses.add(toMenuResponse(menu)));
//        } else if (name != null) {
//            menuRepository.findByNameIgnoreCase(name).forEach(menu -> menuResponses.add(toMenuResponse(menu)));
//        } else if (price != null) {
//            menuRepository.findByPrice(price).forEach(menu -> menuResponses.add(toMenuResponse(menu)));
//        } else {
//            menuRepository.findAll().forEach(menu -> menuResponses.add(toMenuResponse(menu)));
//        }
//        return menuResponses;
//    }

//    @Override
//    public Page<MenuResponse> getAll(Integer page, Integer size, String sort) {
        // page - 1 karena page di jpa itu seperti array index mulai dari 0
        /**
         * Pageable adalah interface yang berfungsi sebagai penampung atau pembungkus informasi pagination yang ingin ambil datanya misal
         *
         * index/nomor halaman (pageNumber) dimulai dari 0 seperti index array/list
         * jumlah data per halaman (pageSize)
         * informasi urutannya/pengurutan (sort) ini optional untuk sorting
         */
        // intinya ada di Pageable ini dan Page<Menu>, kalau mau diurutkan berarti  dan Sort  juga
//        Pageable menuPageable = PageRequest.of((page - 1), size, SortUtil.parseSortFromQueryParam(sort));
//        Page<Menu> menusPage = menuRepository.findAll(menuPageable);
//        return menusPage.map(menu -> toMenuResponse(menu));
//    }


    @Override
    @Transactional(readOnly = true) // ini optimalisasi
    public Page<MenuResponse> getAll(SearchMenuRequest searchMenuRequest) {
        Pageable menuPageable = PageRequest.of(
                (searchMenuRequest.getPage() - 1),
                searchMenuRequest.getSize(),
                SortUtil.parseSortFromQueryParam(searchMenuRequest.getSort())
        );
        Specification<Menu> menuSpecification = MenuSpecification.getSpecification(searchMenuRequest);
        Page<Menu> menusPage = menuRepository.findAll(menuSpecification, menuPageable);
        return menusPage.map(this::toMenuResponse);
    }

    @Override
    public MenuResponse updateMenu(String id, MenuRequest menuRequest) {
        Menu currentMenu = getOne(id);
        currentMenu.setId(id);
        currentMenu.setName(menuRequest.getName());
        currentMenu.setPrice(menuRequest.getPrice());
        currentMenu.setStock(menuRequest.getStock());
        currentMenu.setCategory(MenuCategory.fromValue(menuRequest.getCategory()));
        menuRepository.save(currentMenu);
        return toMenuResponse(currentMenu); // alternative dari toMenuResponse, dan bagusnya itu di setiap DTO atau di Mapper Util (karena ya biar service focus pada core business logicnya)
    }

    @Override
    public Menu updateOne(Menu menu) {
        return menuRepository.saveAndFlush(menu);
    }

    @Override
    public void deleteMenu(String id) {
        Menu menu = getOne(id);
        if (menu.getImages() != null && !menu.getImages().isEmpty()) {
            menu.getImages().forEach(menuImage -> storageService.deleteFile(menuImage.getPublicId()));
        }
        menuRepository.delete(menu); // akan delete images yang berelasi karena (cascade = CascadeType.ALL, orphanRemoval = true)
    }

    // sebagai Mapper dari Menu Entity menjadi MenuResponse DTO
    private MenuResponse toMenuResponse(Menu menu) {
        MenuResponse menuResponse = new MenuResponse();
        menuResponse.setId(String.valueOf(menu.getId()));
        menuResponse.setName(menu.getName());
        menuResponse.setPrice(menu.getPrice());
        menuResponse.setStock(menu.getStock());
        menuResponse.setCategory(menu.getCategory().toString());
        menuResponse.setImages(menu.getImages());
        return menuResponse;
    }
}
