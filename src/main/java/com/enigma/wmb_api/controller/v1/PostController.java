package com.enigma.wmb_api.controller.v1;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.service.impl.PostServiceImpl;
import com.enigma.wmb_api.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/posts")
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostServiceImpl postServiceImpl;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        Object postObject = postServiceImpl.getPostById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success get post by ID", postObject);
    }
}
