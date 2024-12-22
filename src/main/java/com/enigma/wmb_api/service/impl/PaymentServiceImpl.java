package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.constant.PaymentStatus;
import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.dto.request.*;
import com.enigma.wmb_api.dto.response.MidtransSnapResponse;
import com.enigma.wmb_api.dto.response.OrderDetailsResponse;
import com.enigma.wmb_api.dto.response.OrderResponse;
import com.enigma.wmb_api.dto.response.PaymentResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.OrderRepository;
import com.enigma.wmb_api.repository.PaymentRepository;
import com.enigma.wmb_api.service.*;
import com.enigma.wmb_api.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Qualifier("midtransPaymentGatewayClient")
    private final RestClient midtransPaymentGatewayClient;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public PaymentServiceImpl(RestClient midtransPaymentGatewayClient, PaymentRepository paymentRepository, OrderService orderService) {
        this.midtransPaymentGatewayClient = midtransPaymentGatewayClient;
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse createPayment(String orderId) {
        // #Midtrans# 1 ambil order
        Order order = orderService.getOne(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status must be PENDING but current status is " + order.getStatus());
        }

        // #Midtrans# 2 dapetin total amount dari order, total masing-masing item dijumlah
        // bagaimana dapatkan total amount dari berbagai order items/details? bisa pakai reduce bisa pakai
        // https://www.baeldung.com/java-stream-sum
        List<OrderDetails> orderDetailList = order.getOrderDetails();
        Long totalAmount = orderDetailList.stream().mapToLong(orderDetail -> {
            Long totalAmountPerDetail = orderDetail.getMenu().getPrice() * orderDetail.getQuantity();
            return totalAmountPerDetail;
        }).sum();

        // #Midtrans# 3 build MidtransTransactionDetailRequest nya
        MidtransTransactionDetailRequest midtransTransactionDetailRequest = MidtransTransactionDetailRequest.builder()
                .orderId(orderId)
                .grossAmount(totalAmount)
                .build();

        MidtransPaymentRequest midtransRequest = MidtransPaymentRequest.builder()
                .transactionDetail(midtransTransactionDetailRequest)
                .enabledPayments(List.of("gopay", "shopeepay", "bca_va"))
                .build();

        // #Midtrans# 3 request ke midtrans
        MidtransSnapResponse midtransSnapResponse = midtransPaymentGatewayClient
                .post() // http method
                .uri("/snap/v1/transactions") // endpoint path
                .body(midtransRequest)
                .retrieve()
                .body(MidtransSnapResponse.class);

        assert midtransSnapResponse != null;
        Payment payment = Payment.builder()
                .order(order)
                .amount(totalAmount)
                .status(PaymentStatus.PENDING)
                .tokenSnap(midtransSnapResponse.getToken()) // dari midtransSnapResponse
                .redirectUrl(midtransSnapResponse.getRedirectUrl()) // dari midtransSnapResponse
                .build();

        // #Midtrans# 4 save ke table payment
        paymentRepository.saveAndFlush(payment);

        return PaymentResponse.builder()
                .orderId(orderId)
                .amount(totalAmount)
                .paymentStatus(PaymentStatus.PENDING)
                .tokenSnap(midtransSnapResponse.getToken())
                .redirectUrl(midtransSnapResponse.getRedirectUrl())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentNotification(MidtransNotificationRequest request) {
        // 1. dapetin payment dari order id
        Payment payment = paymentRepository.findByOrderId(request.getOrderId());

        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
        }

        // 2. update payment status berdasarkan dari midtrans
        PaymentStatus newPaymentStatus = PaymentStatus.fromStatus(request.getTransactionStatus());

        payment.setStatus(newPaymentStatus);
        paymentRepository.saveAndFlush(payment);

        // 3. update order status
        if (newPaymentStatus == PaymentStatus.SETTLEMENT) {
            UpdateOrderStatusRequest updateOrderStatusToPaid = UpdateOrderStatusRequest.builder().orderStatus(OrderStatus.PAID).build();
            orderService.updateOrderStatus(request.getOrderId(), updateOrderStatusToPaid);
        } else if (
                newPaymentStatus == PaymentStatus.CANCEL ||
                newPaymentStatus == PaymentStatus.DENY ||
                newPaymentStatus == PaymentStatus.EXPIRE
            ) {
            UpdateOrderStatusRequest updateOrderStatusToCanceled = UpdateOrderStatusRequest.builder().orderStatus(OrderStatus.CANCELLED).build();
            orderService.updateOrderStatus(request.getOrderId(), updateOrderStatusToCanceled);
        }
    }
}