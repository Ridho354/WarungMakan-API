package com.enigma.wmb_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsResponse {
    private String id;
    private String menuId;
    private String menuName;
    private Integer quantity;
    private Long totalPrice;
    private Long priceSnapshot;

}
