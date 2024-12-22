package com.enigma.wmb_api.util;

import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseUtil {
    public static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus status, String message, T data) {
        CommonResponse<T> response = new CommonResponse<>(status.value(), message, data);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<CommonResponse<List<T>>> buildPageResponse(HttpStatus status, String message, Page<T> page) {
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPage(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .build();
        CommonResponse<List<T>> response = new CommonResponse<>(status.value(), message, page.getContent(), pagingResponse);
        return ResponseEntity.status(status).body(response);
    }
}
