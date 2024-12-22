package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.constant.OrderStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@SuperBuilder
public class SearchOrderRequest extends PagingRequest {
    private String customerId;
    private String orderId;
    private OrderStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}