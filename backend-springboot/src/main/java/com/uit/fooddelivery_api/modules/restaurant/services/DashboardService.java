package com.uit.fooddelivery_api.modules.restaurant.services;

import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.restaurant.dtos.DashboardResponseDTO;
import com.uit.fooddelivery_api.modules.restaurant.dtos.FoodSaleStatDTO;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.common.utils.OrderRevenueUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;

    public DashboardResponseDTO getMerchantDashboard(User merchant) {
        Long merchantId = merchant.getId();

        // 1. Lấy Tổng doanh thu & Số đơn (theo giá gốc)
        List<Order> completedOrders = orderRepository.findCompletedOrdersByMerchant(merchantId);
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Order order : completedOrders) {
            totalRevenue = totalRevenue.add(OrderRevenueUtil.calculateOriginalRevenue(order));
        }
        Long totalOrders = (long) completedOrders.size();

        // 2. Map dữ liệu thô (Object[]) từ Database sang DTO
        List<Object[]> rawStats = orderRepository.getFoodSalesStatsByMerchant(merchantId);
        List<FoodSaleStatDTO> allStats = new ArrayList<>();

        for (Object[] row : rawStats) {
            allStats.add(FoodSaleStatDTO.builder()
                    .foodId((Long) row[0])
                    .foodName((String) row[1])
                    .totalSold(((Number) row[2]).longValue())
                    .build());
        }

        // 3. Lọc Top 5 Bán chạy nhất (List đã được DB order DESC sẵn)
        List<FoodSaleStatDTO> bestSellers = new ArrayList<>();
        for (int i = 0; i < Math.min(5, allStats.size()); i++) {
            bestSellers.add(allStats.get(i));
        }

        // 4. Lọc Top 5 Ế nhất (Đảo ngược List lại)
        List<FoodSaleStatDTO> worstSellers = new ArrayList<>();
        List<FoodSaleStatDTO> reversedStats = new ArrayList<>(allStats);
        Collections.reverse(reversedStats);
        for (int i = 0; i < Math.min(5, reversedStats.size()); i++) {
            worstSellers.add(reversedStats.get(i));
        }

        // Trả về kết quả tổng hợp
        return DashboardResponseDTO.builder()
                .totalRevenue(totalRevenue)
                .totalCompletedOrders(totalOrders)
                .bestSellers(bestSellers)
                .worstSellers(worstSellers)
                .build();
    }
}