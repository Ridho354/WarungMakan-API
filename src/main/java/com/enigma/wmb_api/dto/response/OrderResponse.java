package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.constant.OrderType;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String id;
    private String customerId;
    private String customerName;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;

    // isinya detail dari orderan (orderDetails)
    private List<OrderDetailsResponse> orderDetails;
}

