package com.enigma.wmb_api.dto.request;


import lombok.*;

//title: 'foo',
//body: 'bar',
//userId: 1,
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoRequest {
    private String title;
    private String body;
    private String userId;
}
