package com.uit.fooddelivery_api.modules.statistic.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.statistic.dtos.MerchantDashboardDTO;
import com.uit.fooddelivery_api.modules.statistic.services.StatisticService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    // API: Dành cho Chủ quán lấy dữ liệu vẽ biểu đồ trang chủ
    @GetMapping("/merchant/dashboard")
    public ApiResponse<MerchantDashboardDTO> getMerchantDashboard(Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        MerchantDashboardDTO dashboardData = statisticService.getMerchantDashboard(merchant);

        return ApiResponse.success(dashboardData);
    }
}