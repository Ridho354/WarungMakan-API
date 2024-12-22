package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.constant.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DraftOrderRequest {
    private String customerId;
    private OrderType orderType;
}
