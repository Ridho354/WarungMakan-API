package com.enigma.wmb_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PostServiceImpl {
    private final RestTemplate restTemplate;

    @Value("${wmb_api.json_placeholder_url}")
    private String jsonPlaceholderBaseUrl;

    public Object getPostById(Integer postId) {
        String requestUrl = jsonPlaceholderBaseUrl+"/posts/"+postId;
        return restTemplate.getForObject(requestUrl, Object.class);
    }
}
