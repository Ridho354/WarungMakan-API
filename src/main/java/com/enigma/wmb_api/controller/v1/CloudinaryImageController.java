package com.enigma.wmb_api.controller.v1;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.service.CloudinaryService;
import com.enigma.wmb_api.service.StorageService;
import com.enigma.wmb_api.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/v1/images")
@RequestMapping(Constant.IMAGES_TEST_API)
public class CloudinaryImageController {
    private final StorageService storageService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam(value = "folder", defaultValue = "wmb") String folder) {
        Map result = storageService.uploadFile(multipartFile, folder);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully uploaded image", result);
    }

    // ini adalah solusi ketika path variable kemungkinan ada special characters seperti slash
    // alternative gunakan query param sebagai workaround
    @DeleteMapping("/**")
//    public ResponseEntity<?> deleteImage(@PathVariable("publicId") String publicId) {
    public ResponseEntity<?> deleteImage(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();

//        String publicId = requestURL.split("/api/v1/images/")[1];
        String publicId = requestURL.split(Constant.IMAGES_TEST_API)[1]; // pentingnya Constant

        storageService.deleteFile(publicId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfuly deleted image", null);
    }
}
