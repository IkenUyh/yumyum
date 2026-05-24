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
}