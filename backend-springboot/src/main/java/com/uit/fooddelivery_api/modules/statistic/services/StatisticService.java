package com.uit.fooddelivery_api.modules.statistic.services;

import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.statistic.dtos.MerchantDashboardDTO;
import com.uit.fooddelivery_api.modules.statistic.dtos.TopFoodDTO;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final OrderRepository orderRepository;

    public MerchantDashboardDTO getMerchantDashboard(User merchant) {
        Long merchantId = merchant.getId();

        // 1. Lấy tổng doanh thu
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenueByMerchant(merchantId);

        // 2. Lấy tổng số đơn hoàn thành
        Long totalOrders = orderRepository.countCompletedOrdersByMerchant(merchantId);

        // 3. Xử lý danh sách món bán chạy (Ép kiểu an toàn từ Object[] của Hibernate)
        List<Object[]> rawFoodStats = orderRepository.getFoodSalesStatsByMerchant(merchantId);
        List<TopFoodDTO> topFoods = new ArrayList<>();

        for (Object[] row : rawFoodStats) {
            Long foodId = ((Number) row[0]).longValue();
            String foodName = (String) row[1];
            Long totalSold = ((Number) row[2]).longValue();
            topFoods.add(new TopFoodDTO(foodId, foodName, totalSold));
        }

        // Trả về DTO tổng hợp
        return MerchantDashboardDTO.builder()
                .totalRevenue(totalRevenue)
                .totalCompletedOrders(totalOrders)
                .topSellingFoods(topFoods)
                .build();
    }
}