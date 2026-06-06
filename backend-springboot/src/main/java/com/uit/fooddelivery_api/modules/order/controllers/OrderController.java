package com.uit.fooddelivery_api.modules.order.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.order.dtos.CreateOrderDTO;
import com.uit.fooddelivery_api.modules.order.dtos.OrderResponseDTO;
import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.services.OrderService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponseDTO> createOrder(
            Authentication authentication,
            @RequestBody CreateOrderDTO dto) {

        User customer = (User) authentication.getPrincipal();
        Order savedOrder = orderService.createOrder(dto, customer);

        return ApiResponse.success(OrderResponseDTO.fromEntity(savedOrder));
    }

    // API Lấy danh sách đơn chờ nhận
    @GetMapping("/available")
    public ApiResponse<java.util.List<OrderResponseDTO>> getAvailableOrders() {
        java.util.List<OrderResponseDTO> list = orderService.getAvailableOrders()
                .stream()
                .map(OrderResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    // API Tài xế nhận đơn
    @PutMapping("/{id}/accept")
    public ApiResponse<OrderResponseDTO> acceptOrder(
            @PathVariable("id") Long orderId,
            Authentication authentication) {
        User driver = (User) authentication.getPrincipal();
        Order order = orderService.acceptOrder(orderId, driver);
        return ApiResponse.success(OrderResponseDTO.fromEntity(order));
    }

    // API Tài xế hoàn thành đơn
    @PutMapping("/{id}/complete")
    public ApiResponse<OrderResponseDTO> completeOrder(
            @PathVariable("id") Long orderId,
            Authentication authentication) {
        User driver = (User) authentication.getPrincipal();
        Order order = orderService.completeOrder(orderId, driver);
        return ApiResponse.success(OrderResponseDTO.fromEntity(order));
    }

    // API xem lịch sử đơn hàng của Khách (Customer)
    @GetMapping("/history/customer")
    public ApiResponse<java.util.List<OrderResponseDTO>> getCustomerHistory(
            Authentication authentication) {
        User customer = (User) authentication.getPrincipal();
        java.util.List<OrderResponseDTO> list = orderService.getCustomerOrderHistory(customer)
                .stream()
                .map(OrderResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    // API xem lịch sử đơn bán của Chủ quán (Merchant)
    @GetMapping("/history/merchant")
    public ApiResponse<java.util.List<OrderResponseDTO>> getMerchantHistory(
            Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        java.util.List<OrderResponseDTO> list = orderService.getMerchantOrderHistory(merchant)
                .stream()
                .map(OrderResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }
}