package com.uit.fooddelivery_api.modules.order.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.order.dtos.CancelOrderDTO;
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

    @PostMapping("/preview")
    public ApiResponse<com.uit.fooddelivery_api.modules.order.dtos.OrderPreviewResponseDTO> previewOrder(
            Authentication authentication,
            @RequestBody CreateOrderDTO dto) {

        User customer = (User) authentication.getPrincipal();
        com.uit.fooddelivery_api.modules.order.dtos.OrderPreviewResponseDTO previewData = orderService.previewOrder(dto, customer);

        return ApiResponse.success(previewData);
    }

    @PostMapping
    public ApiResponse<OrderResponseDTO> createOrder(
            Authentication authentication,
            @RequestBody CreateOrderDTO dto) {

        User customer = (User) authentication.getPrincipal();
        Order savedOrder = orderService.createOrder(dto, customer);

        return ApiResponse.success(OrderResponseDTO.fromEntity(savedOrder));
    }

    // API Hủy Đơn Hàng
    @PutMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponseDTO> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody CancelOrderDTO dto,
            Authentication authentication) {

        User actionUser = (User) authentication.getPrincipal();

        // Truyền lý do vào (nếu khách không nhập thì để mặc định)
        String reason = (dto.getReason() != null && !dto.getReason().trim().isEmpty())
                ? dto.getReason()
                : "Không có lý do";

        Order cancelledOrder = orderService.cancelOrder(orderId, actionUser, reason);

        return ApiResponse.success(OrderResponseDTO.fromEntity(cancelledOrder));
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

    // 1. API: Chủ quán xác nhận đã chuẩn bị xong đồ ăn
    @PutMapping("/{orderId}/merchant-ready")
    public ApiResponse<OrderResponseDTO> merchantReady(
            @PathVariable Long orderId,
            Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        Order order = orderService.merchantCompletePreparation(orderId, merchant);
        return ApiResponse.success(OrderResponseDTO.fromEntity(order));
    }

    // 2. API: Tài xế nhập mã lấy đồ tại quán để đi giao
    @PutMapping("/{orderId}/driver-pickup")
    public ApiResponse<OrderResponseDTO> driverPickup(
            @PathVariable Long orderId,
            @RequestBody com.uit.fooddelivery_api.modules.order.dtos.ConfirmPickupDTO dto,
            Authentication authentication) {
        User driver = (User) authentication.getPrincipal();
        Order order = orderService.driverConfirmPickup(orderId, driver, dto.getPickupCode());
        return ApiResponse.success(OrderResponseDTO.fromEntity(order));
    }

    // 3. API: Tài xế nhập mã PIN của khách để hoàn thành đơn hàng
    @PutMapping("/{orderId}/driver-complete")
    public ApiResponse<OrderResponseDTO> driverComplete(
            @PathVariable Long orderId,
            @RequestBody com.uit.fooddelivery_api.modules.order.dtos.ConfirmDeliveryDTO dto,
            Authentication authentication) {
        User driver = (User) authentication.getPrincipal();
        Order order = orderService.completeOrderSecure(orderId, driver, dto.getDeliveryPin());
        return ApiResponse.success(OrderResponseDTO.fromEntity(order));
    }

    // 4. API: Chủ quán xóa 1 món khỏi đơn hàng đang xử lý
    @PutMapping("/{orderId}/remove-item")
    public ApiResponse<OrderResponseDTO> removeItem(
            @PathVariable Long orderId,
            @RequestBody com.uit.fooddelivery_api.modules.order.dtos.RemoveItemDTO dto,
            Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        Order order = orderService.removeItemFromOrder(orderId, dto.getFoodId(), merchant);
        return ApiResponse.success(OrderResponseDTO.fromEntity(order));
    }

    // API: Lấy chi tiết đơn hàng theo ID
    @GetMapping("/{id}")
    public ApiResponse<OrderResponseDTO> getOrderById(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        return ApiResponse.success(OrderResponseDTO.fromEntity(order));
    }
}