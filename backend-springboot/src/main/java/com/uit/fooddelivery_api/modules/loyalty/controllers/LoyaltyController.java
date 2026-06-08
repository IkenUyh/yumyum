package com.uit.fooddelivery_api.modules.loyalty.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.loyalty.dtos.LoyaltyResponseDTO;
import com.uit.fooddelivery_api.modules.loyalty.entities.LoyaltyPoint;
import com.uit.fooddelivery_api.modules.loyalty.services.LoyaltyService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    // API: Xem thông tin Xu hiện tại
    @GetMapping("/me")
    public ApiResponse<LoyaltyResponseDTO> getMyLoyaltyInfo(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        LoyaltyPoint lp = loyaltyService.getMyLoyaltyInfo(currentUser);
        boolean canCheckIn = loyaltyService.canCheckInToday(lp);

        return ApiResponse.success(LoyaltyResponseDTO.fromEntity(lp, canCheckIn));
    }

    // API: Bấm nút Điểm danh nhận Xu
    @PostMapping("/checkin")
    public ApiResponse<LoyaltyResponseDTO> dailyCheckIn(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        LoyaltyPoint updatedLp = loyaltyService.dailyCheckIn(currentUser);

        // Sau khi điểm danh thành công thì chắc chắn hôm nay không được điểm danh nữa
        return ApiResponse.success(LoyaltyResponseDTO.fromEntity(updatedLp, false));
    }
}