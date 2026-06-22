package com.uit.fooddelivery_api.modules.statistic.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.statistic.dtos.MerchantDashboardDTO;
import com.uit.fooddelivery_api.modules.statistic.dtos.MerchantDailyStatisticDTO;
import com.uit.fooddelivery_api.modules.statistic.dtos.MerchantMonthlyStatisticDTO;
import com.uit.fooddelivery_api.modules.statistic.services.StatisticService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;

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

    // API: Dành cho Chủ quán lấy dữ liệu thống kê doanh thu và lượt giao dịch hàng ngày theo cửa hàng
    @GetMapping("/merchant/daily")
    public ApiResponse<MerchantDailyStatisticDTO> getMerchantDailyStatistic(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam(value = "date", required = false) String dateStr,
            Authentication authentication) {
        LocalDate date;
        // Dùng ZoneId tường minh thay vì phụ thuộc JVM default timezone
        LocalDate todayVn = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        if (dateStr == null || dateStr.trim().isEmpty()) {
            date = todayVn;
        } else {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                date = todayVn;
            }
        }
        MerchantDailyStatisticDTO dailyData = statisticService.getMerchantDailyStatistic(restaurantId, date);
        return ApiResponse.success(dailyData);
    }

    // API: Dành cho Chủ quán lấy thống kê doanh thu theo tháng (chi tiết từng ngày)
    @GetMapping("/merchant/monthly")
    public ApiResponse<MerchantMonthlyStatisticDTO> getMerchantMonthlyStatistic(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("month") int month,
            @RequestParam("year") int year,
            Authentication authentication) {
        MerchantMonthlyStatisticDTO monthlyData = statisticService.getMerchantMonthlyStatistic(restaurantId, month, year);
        return ApiResponse.success(monthlyData);
    }
}