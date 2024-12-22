package com.enigma.wmb_api.dto.response;

import lombok.Builder;
import lombok.Data;

// data dari data (meta data)
@Data
@Builder
public class PagingResponse {
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private Long totalItems;
}
