package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.constant.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String customerId;
    private OrderType orderType;
}
