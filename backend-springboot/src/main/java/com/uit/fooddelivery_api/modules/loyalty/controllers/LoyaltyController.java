package com.uit.fooddelivery_api.modules.loyalty.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.loyalty.dtos.LoyaltyResponseDTO;
import com.uit.fooddelivery_api.modules.loyalty.dtos.DealHistoryResponseDTO;
import com.uit.fooddelivery_api.modules.loyalty.entities.LoyaltyPoint;
import com.uit.fooddelivery_api.modules.loyalty.services.LoyaltyService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @GetMapping("/me")
    public ApiResponse<LoyaltyResponseDTO> getMyLoyaltyInfo(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        LoyaltyPoint lp = loyaltyService.getMyLoyaltyInfo(currentUser);
        boolean canCheckIn = loyaltyService.canCheckInToday(lp);
        String rankName = loyaltyService.getRankName(lp.getTotalSpending());

        return ApiResponse.success(LoyaltyResponseDTO.fromEntity(lp, canCheckIn, rankName));
    }

    // API: Bấm nút Điểm danh nhận Xu
    @PostMapping("/checkin")
    public ApiResponse<LoyaltyResponseDTO> dailyCheckIn(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        LoyaltyPoint updatedLp = loyaltyService.dailyCheckIn(currentUser);

        String rankName = loyaltyService.getRankName(updatedLp.getTotalSpending());
        return ApiResponse.success(LoyaltyResponseDTO.fromEntity(updatedLp, false, rankName));
    }

    // API: Xem danh sách Deal đã mua
    @GetMapping("/deals")
    public ApiResponse<List<DealHistoryResponseDTO>> getMyDeals(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<DealHistoryResponseDTO> deals = loyaltyService.getMyDeals(currentUser);
        return ApiResponse.success(deals);
    }
}