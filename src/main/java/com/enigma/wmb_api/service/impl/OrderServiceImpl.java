package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.dto.request.*;
import com.enigma.wmb_api.dto.response.*;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.OrderRepository;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.service.OrderService;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final MenuService menuService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createDraftOrder(DraftOrderRequest request) {
        Customer customer = customerService.getOne(request.getCustomerId());

        if (!isCustomerAllowedToAccess(customer.getUserAccount().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, Constant.ERROR_UNAUTHORIZED_ORDER_CREATE);
        }

        Order draftOrder = Order.builder()
                .customer(customer)
                .orderType(request.getOrderType())
                .status(OrderStatus.DRAFT)
                .orderDetails(new ArrayList<>())
                .build();

        Order savedOrder = orderRepository.save(draftOrder);
        return toOrderResponse(savedOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse addOrderDetails(String orderId, OrderDetailsRequest orderDetailsRequest) {
        // Mendapatkan order berdasarkan ID
        Order order = getOne(orderId);

        if (!isCustomerAllowedToAccess(order.getCustomer().getUserAccount().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, Constant.ERROR_UNAUTHORIZED_ORDER_MODIFY);
        }

        // Hanya bisa menambahkan item ke dalam order jika statusnya DRAFT
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_DRAFT_ONLY);
        }

        // Mendapatkan data menu berdasarkan menuId dari request
        Menu menu = menuService.getOne(orderDetailsRequest.getMenuId());

        // Validate menu stock
        if (menu.getStock() < orderDetailsRequest.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(Constant.ERROR_INSUFFICIENT_STOCK, menu.getStock(), orderDetailsRequest.getQuantity()));
        }

        // Mengecek apakah menu yang sama sudah ada di order details
        Optional<OrderDetails> existingOrderDetail = order.getOrderDetails()
                .stream()
                .filter(detail -> detail.getMenu().getId().equals(menu.getId()))
                .findFirst();

        if (existingOrderDetail.isPresent()) {
            // Jika menu sudah ada di order, tambahkan kuantitasnya
            OrderDetails orderDetails = existingOrderDetail.get();
            orderDetails.setQuantity(orderDetails.getQuantity() + orderDetailsRequest.getQuantity());
            orderDetails.setPriceSnapshot(menu.getPrice()); // Mengupdate harga snapshot jika diperlukan
            // perbarui total harga
            orderDetails.setTotalPrice(orderDetails.getQuantity() * menu.getPrice());
        } else {
            // Jika menu belum ada, buat detail order baru dan tambahkan ke order
            OrderDetails newOrderDetails = OrderDetails.builder()
                    .menu(menu) // Set menu yang dipesan
                    .order(order) // Hubungkan dengan order saat ini
                    .quantity(orderDetailsRequest.getQuantity()) // Set kuantitas dari request
                    .priceSnapshot(menu.getPrice()) // Simpan harga menu saat ini
                    .totalPrice(menu.getPrice() * orderDetailsRequest.getQuantity())
                    .build();

            // Tambahkan detail order baru ke daftar order
            order.getOrderDetails().add(newOrderDetails);
        }

        // Simpan perubahan ke database
        Order updatedOrder = orderRepository.save(order);

        return toOrderResponse(updatedOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse updateOrderDetails(String orderId, String detailId, OrderDetailsRequest request) {
        // Mendapatkan order dan validasi akses
        Order order = validateOrderAndAccess(orderId);

        // Validasi status order
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_UPDATE_DRAFT_ONLY);
        }

        // Mencari detail order yang akan diupdate
        OrderDetails orderDetail = findOrderDetail(order, detailId);
        Menu menu = menuService.getOne(request.getMenuId());

        // Validasi stock
        if (menu.getStock() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(Constant.ERROR_INSUFFICIENT_STOCK_MENU, menu.getName()));
        }

        // Update detail order
        orderDetail.setMenu(menu);
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setPriceSnapshot(menu.getPrice());
        orderDetail.setTotalPrice(menu.getPrice() * request.getQuantity());

        return toOrderResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse deleteOrderDetails(String orderId, String detailId) {
        // Mendapatkan order dan validasi akses
        Order order = validateOrderAndAccess(orderId);

        // Validasi status order
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_REMOVE_DRAFT_ONLY);
        }

        // Hapus detail order
        order.getOrderDetails().removeIf(detail -> detail.getId().equals(detailId));
        return toOrderResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        // Mendapatkan order dan validasi akses
        Order order = validateOrderAndAccess(orderId);

        // Validasi transisi status
        validateStatusTransition(order.getStatus(), request.getOrderStatus());

        order.setStatus(request.getOrderStatus());
        return toOrderResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse checkoutOrder(String orderId) {
        // Mendapatkan order dan validasi akses
        Order order = validateOrderAndAccess(orderId);

        // Validasi status order
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_CHECKOUT_DRAFT_ONLY);
        }

        // Validasi order memiliki items
        if (order.getOrderDetails().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ERROR_EMPTY_ORDER_CHECKOUT);
        }

        // Validasi dan update stock untuk semua items
        for (OrderDetails orderDetail : order.getOrderDetails()) {
            Menu menu = orderDetail.getMenu();
            if (menu.getStock() < orderDetail.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format(Constant.ERROR_INSUFFICIENT_STOCK_MENU, menu.getName()));
            }
            menu.setStock(menu.getStock() - orderDetail.getQuantity());
            menuService.updateOne(menu);
        }

        order.setStatus(OrderStatus.PENDING);
        return toOrderResponse(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponse> searchOrders(SearchOrderRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        return orderRepository.findAll(OrderSpecification.getSpecification(request), pageable)
                .map(this::toOrderResponse);
    }

    @Override
    public Page<OrderResponse> getCustomerOrders(String customerId, int page, int size) {
        if (!isCustomerAllowedToAccess(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, Constant.ERROR_UNAUTHORIZED_ORDER_ACCESS);
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        return orderRepository.findAll(OrderSpecification.getCustomerOrdersSpecification(customerId), pageable)
                .map(this::toOrderResponse);
    }

    @Override
    public List<OrderDetailsResponse> getOrderDetails(String orderId) {
        // Mendapatkan order dan validasi akses
        Order order = validateOrderAndAccess(orderId);
        return order.getOrderDetails().stream()
                .map(this::toOrderDetailsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Order getOne(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.ERROR_ORDER_NOT_FOUND));
    }

    @Override
    public boolean isCustomerAllowedToAccess(String customerUserId) {
        // #SPRING SECURITY# 11
        UserAccount currentUser = userService.getByContext();

        // #SPRING SECURITY# 11 validasi tambahan di level service
        if (currentUser.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return true;
        }

        // #SPRING SECURITY# 11 validasi tambahan di level service
        if (currentUser.getRole() == UserRole.ROLE_CUSTOMER) {
            return customerUserId.equals(currentUser.getId());
        }

        return false;
    }

    private Order validateOrderAndAccess(String orderId) {
        Order order = getOne(orderId);

        // TODO: FIX
//        if (!isCustomerAllowedToAccess(order.getCustomer().getUserAccount().getId())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, Constant.ERROR_UNAUTHORIZED_ORDER_ACCESS);
//        }

        return order;
    }

    private OrderDetails findOrderDetail(Order order, String detailId) {
        return order.getOrderDetails().stream()
                .filter(detail -> detail.getId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.ERROR_ORDER_DETAIL_NOT_FOUND));
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        boolean isValid = false;

        switch(currentStatus) {
            case DRAFT:
                isValid = newStatus == OrderStatus.PENDING || newStatus == OrderStatus.CANCELLED;
                break;
            case PENDING:
                isValid = newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
                break;
            case PAID:
                isValid = newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED;
                break;
            case COMPLETED:
            case CANCELLED:
                isValid = false;
                break;
        }

        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(Constant.ERROR_INVALID_STATUS_TRANSITION, currentStatus, newStatus));
        }
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderDetailsResponse> orderDetailsResponse = order.getOrderDetails().stream()
                .map(this::toOrderDetailsResponse).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .orderType(order.getOrderType())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getStatus())
                .orderDetails(orderDetailsResponse)
                .build();
    }

    private OrderDetailsResponse toOrderDetailsResponse(OrderDetails orderDetails) {
        return OrderDetailsResponse.builder()
                .id(orderDetails.getId())
                .menuId(orderDetails.getMenu().getId())
                .menuName(orderDetails.getMenu().getName())
                .quantity(orderDetails.getQuantity())
                .priceSnapshot(orderDetails.getPriceSnapshot())
                .totalPrice(orderDetails.getTotalPrice())
                .build();
    }
}