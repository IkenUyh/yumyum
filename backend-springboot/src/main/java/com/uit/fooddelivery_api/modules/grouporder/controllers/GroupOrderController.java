package com.uit.fooddelivery_api.modules.grouporder.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.grouporder.entities.GroupOrder;
import com.uit.fooddelivery_api.modules.grouporder.services.GroupOrderService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/group-orders")
@RequiredArgsConstructor
public class GroupOrderController {

    private final GroupOrderService groupOrderService;

    // 1. Tạo phòng
    @PostMapping("/create")
    public ApiResponse<String> createRoom(@RequestParam Long restaurantId, Authentication authentication) {
        User host = (User) authentication.getPrincipal();
        GroupOrder room = groupOrderService.createRoom(restaurantId, host);
        return ApiResponse.success("Tạo phòng thành công! Mã phòng của bạn là: " + room.getRoomCode());
    }

    // 2. Thêm món vào phòng
    @PostMapping("/{roomCode}/add-item")
    public ApiResponse<String> addItem(
            @PathVariable String roomCode,
            @RequestParam Long foodId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String optionsJson,
            Authentication authentication) {

        User member = (User) authentication.getPrincipal();
        groupOrderService.addItemToRoom(roomCode, foodId, quantity, optionsJson, member);
        return ApiResponse.success("Đã thêm món vào phòng chung thành công!");
    }

    // 3. Chốt đơn và Split Bill
    @PostMapping("/{roomCode}/checkout")
    public ApiResponse<Map<String, Object>> checkoutAndSplitBill(
            @PathVariable String roomCode,
            @RequestParam BigDecimal totalShippingFee, // Host truyền tiền ship vào đây để chia đều
            Authentication authentication) {

        User host = (User) authentication.getPrincipal();
        Map<String, Object> billDetails = groupOrderService.closeRoomAndSplitBill(roomCode, host, totalShippingFee);
        return ApiResponse.success(billDetails);
    }
}