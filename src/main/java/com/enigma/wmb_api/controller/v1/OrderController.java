package com.enigma.wmb_api.controller.v1;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.dto.request.*;
import com.enigma.wmb_api.dto.response.*;
import com.enigma.wmb_api.service.OrderService;
import com.enigma.wmb_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Constant.ORDER_API)
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/draft")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createDraftOrder(@RequestBody DraftOrderRequest request) {
        OrderResponse orderResponse = orderService.createDraftOrder(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, Constant.SUCCESS_CREATE_DRAFT_ORDER, orderResponse);
    }

    @PostMapping("/{orderId}/details")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addOrderDetails(@PathVariable String orderId, @RequestBody OrderDetailsRequest request) {
        OrderResponse orderResponse = orderService.addOrderDetails(orderId, request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, Constant.SUCCESS_ADD_ORDER_DETAILS, orderResponse);
    }

    @GetMapping("/{orderId}/details")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
        List<OrderDetailsResponse> orderDetails = orderService.getOrderDetails(orderId);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_GET_ORDER_DETAILS, orderDetails);
    }

    @PatchMapping("/{orderId}/details/{detailId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateOrderDetail(
            @PathVariable String orderId,
            @PathVariable String detailId,
            @RequestBody OrderDetailsRequest request
    ) {
        OrderResponse orderResponse = orderService.updateOrderDetails(orderId, detailId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Order detail updated successfully", orderResponse);
    }

    @DeleteMapping("/{orderId}/details/{detailId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> removeOrderDetail(
            @PathVariable String orderId,
            @PathVariable String detailId
    ) {
        OrderResponse orderResponse = orderService.deleteOrderDetails(orderId, detailId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Order detail removed successfully", orderResponse);
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        OrderResponse orderResponse = orderService.updateOrderStatus(orderId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_UPDATE_ORDER_STATUS, orderResponse);
    }

    @PostMapping("/{orderId}/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> checkoutOrder(@PathVariable String orderId) {
        OrderResponse orderResponse = orderService.checkoutOrder(orderId);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_CHECKOUT_ORDER, orderResponse);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> searchOrders(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SearchOrderRequest request = SearchOrderRequest.builder()
                .customerId(customerId)
                .orderId(orderId)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        Page<OrderResponse> orders = orderService.searchOrders(request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Orders retrieved successfully", orders);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getCustomerOrders(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponse> orders = orderService.getCustomerOrders(customerId, page, size);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Customer orders retrieved successfully", orders);
    }
}