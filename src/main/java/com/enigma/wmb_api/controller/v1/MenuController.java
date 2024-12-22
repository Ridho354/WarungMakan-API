package com.enigma.wmb_api.controller.v1;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(Constant.MENU_API)
@RestController
@RequiredArgsConstructor
@Validated // #SPRING VALIDATION# annotation ini digunakan untuk mengenablekan/ agar bisa class ataupun method itu di berika validasi
public class MenuController {
    private final MenuService menuService;
    private final ObjectMapper objectMapper;

    // #SPRING SECURITY# 10
    // dibandingkan hasRole, yang hanya untuk mengecek satu role Specific
    // hasAnyRole itu untuk mengecek beberapa role sekaligus (sebagai kondisi OR)
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    // #SPRING VALIDATION# tambahin @Valid
//    public ResponseEntity<?> createNewMenu(// @Valid @RequestBody MenuRequest request) {
//    public ResponseEntity<?> createNewMenu(@Valid @RequestBody MenuRequest request) {
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')") // kependekan dari yang diatas ->> @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNewMenu(
            @RequestParam(name = "images", required = false) List<MultipartFile> images, //optional karena required=false
            @RequestPart(name = "menu") String menuJson
    ) {
        try {
            MenuRequest menuRequest = objectMapper.readValue(menuJson, MenuRequest.class);
            MenuResponse createdMenu = menuService.createMenu(menuRequest, images);
            return ResponseUtil.buildResponse(HttpStatus.CREATED, Constant.SUCCESS_CREATE_MENU, createdMenu);
        } catch (Exception e) {
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseEntity<?>  getAllMenu(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) Long minPrice,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice,
            @RequestParam(name = "isReady", required = false) Boolean isReady,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sort", required = false, defaultValue = "name") String sort
            ) {
        SearchMenuRequest searchMenuRequest = SearchMenuRequest.builder()
                .name(name)
                .category(category)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .isReady(isReady)
                .page(page)
                .size(size)
                .sort(sort)
                .build();
        Page<MenuResponse> menuPage = menuService.getAll(searchMenuRequest);
        return ResponseUtil.buildPageResponse(HttpStatus.OK, Constant.SUCCESS_GET_PAGINATED_MENU, menuPage);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getMenu(@PathVariable String id) {
        MenuResponse singleMenu = menuService.getMenuById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_GET_MENU, singleMenu);
    }

    // TODO: untuk update images itu harus lewat endpoint terpisah misal path menus/{id}/images/{id}
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateMenu(@PathVariable String id, @RequestBody MenuRequest request) {
        MenuResponse updatedMenu = menuService.updateMenu(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_UPDATE_MENU, updatedMenu);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteMenu(@PathVariable String id) {
        menuService.deleteMenu(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_DELETE_MENU, null);
    }
}
