package com.enigma.wmb_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//"userId": 1,
//        "id": 1,
//        "title": "delectus aut autem",
//        "completed": false
public class TodoResponse {
    private String userId;
    private String id;
    private String title;
    private Boolean completed;
}
