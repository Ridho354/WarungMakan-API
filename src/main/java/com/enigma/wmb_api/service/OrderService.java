package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.DraftOrderRequest;
import com.enigma.wmb_api.dto.request.OrderDetailsRequest;
import com.enigma.wmb_api.dto.request.SearchOrderRequest;
import com.enigma.wmb_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.wmb_api.dto.response.OrderDetailsResponse;
import com.enigma.wmb_api.dto.response.OrderResponse;
import com.enigma.wmb_api.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderResponse createDraftOrder(DraftOrderRequest request);
    OrderResponse addOrderDetails(String orderId, OrderDetailsRequest request);
    List<OrderDetailsResponse> getOrderDetails(String orderId);
    Order getOne(String orderId);

    // Order yang bisa diupdate hanya yang status PENDING / UNPAID
    OrderResponse updateOrderDetails(String orderId, String detailId, OrderDetailsRequest orderDetailsRequest);

    OrderResponse deleteOrderDetails(String orderId, String detailId);
    OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest updateOrderStatusRequest);
    OrderResponse checkoutOrder(String orderId);
    Page<OrderResponse> searchOrders(SearchOrderRequest request);
    Page<OrderResponse> getCustomerOrders(String customerId, int page, int size);
    boolean isCustomerAllowedToAccess(String customerId);
}
