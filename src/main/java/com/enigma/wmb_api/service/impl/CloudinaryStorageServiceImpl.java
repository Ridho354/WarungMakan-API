package com.enigma.wmb_api.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Url;
import com.cloudinary.utils.ObjectUtils;
import com.enigma.wmb_api.service.CloudinaryService;
import com.enigma.wmb_api.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.service.type", havingValue = "CLOUDINARY")
public class CloudinaryStorageServiceImpl implements StorageService {
//public class CloudinaryStorageServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public Map<String, String> uploadFile(MultipartFile multipartFile, String folderName) {
        try {
//            File uploadedFile = convertMultipartToFile(multipartFile);
            Map params = ObjectUtils.asMap(
                    "folder", folderName,
                    "use_filename", true,
                    "resource_type", "auto"
                    );
            Map uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), params);

//            uploadedFile.delete();

            return Map.of(
                    "public_id", (String) uploadResult.get("public_id"),
                    "secure_url", (String) uploadResult.get("secure_url")
            );

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please upload the right file format, error when uploading the file");
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when deleting the file");
        }
    }

    @Override
    public Resource downloadFile(String url) {
        try {
            return new UrlResource(new URL(url));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when loading the file");
        }
    }

//    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
//        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write(multipartFile.getBytes());
//        fos.close();
//        return file;
//    }
}
