package com.uit.fooddelivery_api.modules.restaurant.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.restaurant.dtos.DashboardResponseDTO;
import com.uit.fooddelivery_api.modules.restaurant.services.DashboardService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/merchant")
    public ApiResponse<DashboardResponseDTO> getMerchantDashboard(Authentication authentication) {
        // Lấy thông tin chủ quán đang đăng nhập
        User merchant = (User) authentication.getPrincipal();

        // Kéo dữ liệu thống kê
        DashboardResponseDTO dashboardData = dashboardService.getMerchantDashboard(merchant);

        return ApiResponse.success(dashboardData);
    }
}