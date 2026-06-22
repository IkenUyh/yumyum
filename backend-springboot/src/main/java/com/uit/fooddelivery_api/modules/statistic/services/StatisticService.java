package com.uit.fooddelivery_api.modules.statistic.services;

import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.statistic.dtos.DailyRevenueStatDTO;
import com.uit.fooddelivery_api.modules.statistic.dtos.MerchantDashboardDTO;
import com.uit.fooddelivery_api.modules.statistic.dtos.MerchantDailyStatisticDTO;
import com.uit.fooddelivery_api.modules.statistic.dtos.MerchantMonthlyStatisticDTO;
import com.uit.fooddelivery_api.modules.statistic.dtos.TopFoodDTO;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.common.utils.OrderRevenueUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final OrderRepository orderRepository;

    public MerchantDailyStatisticDTO getMerchantDailyStatistic(Long restaurantId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Order> completedOrders = orderRepository.findCompletedOrdersByRestaurantAndDate(restaurantId, startOfDay, endOfDay);
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Order order : completedOrders) {
            totalRevenue = totalRevenue.add(OrderRevenueUtil.calculateOriginalRevenue(order));
        }
        long transactionCount = completedOrders.size();

        return MerchantDailyStatisticDTO.builder()
                .totalRevenue(totalRevenue)
                .transactionCount(transactionCount)
                .build();
    }

    public MerchantMonthlyStatisticDTO getMerchantMonthlyStatistic(Long restaurantId, int month, int year) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        LocalDateTime endOfMonth = LocalDateTime.of(year, month, daysInMonth, 23, 59, 59, 999999999);

        List<Order> completedOrders = orderRepository.findCompletedOrdersByRestaurantAndDate(restaurantId, startOfMonth, endOfMonth);

        // Group by day
        Map<Integer, BigDecimal> dailyRevenueMap = new HashMap<>();
        Map<Integer, Long> dailyTxCountMap = new HashMap<>();
        
        for (Order order : completedOrders) {
            int day = order.getCreatedAt().getDayOfMonth();
            BigDecimal rev = OrderRevenueUtil.calculateOriginalRevenue(order);
            
            dailyRevenueMap.put(day, dailyRevenueMap.getOrDefault(day, BigDecimal.ZERO).add(rev));
            dailyTxCountMap.put(day, dailyTxCountMap.getOrDefault(day, 0L) + 1L);
        }

        // Determine how many days to include (cap at today for current month)
        LocalDate today = LocalDate.now();
        int maxDay = daysInMonth;
        if (year == today.getYear() && month == today.getMonthValue()) {
            maxDay = Math.min(today.getDayOfMonth(), daysInMonth);
        }

        List<DailyRevenueStatDTO> dailyStats = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal highestRevenue = BigDecimal.ZERO;
        BigDecimal lowestRevenue = null;
        long totalTransactions = 0;

        for (int d = 1; d <= maxDay; d++) {
            BigDecimal revenue = dailyRevenueMap.getOrDefault(d, BigDecimal.ZERO);
            long txCount = dailyTxCountMap.getOrDefault(d, 0L);
            
            dailyStats.add(new DailyRevenueStatDTO(d, revenue, txCount));
            totalRevenue = totalRevenue.add(revenue);
            totalTransactions += txCount;
            if (revenue.compareTo(highestRevenue) > 0) highestRevenue = revenue;
            if (lowestRevenue == null || revenue.compareTo(lowestRevenue) < 0) lowestRevenue = revenue;
        }

        BigDecimal avgRevenue = maxDay > 0
                ? totalRevenue.divide(BigDecimal.valueOf(maxDay), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        if (lowestRevenue == null) lowestRevenue = BigDecimal.ZERO;

        return MerchantMonthlyStatisticDTO.builder()
                .month(month)
                .year(year)
                .totalRevenue(totalRevenue)
                .avgDailyRevenue(avgRevenue)
                .highestDailyRevenue(highestRevenue)
                .lowestDailyRevenue(lowestRevenue)
                .totalTransactions(totalTransactions)
                .dailyStats(dailyStats)
                .build();
    }

    public MerchantDashboardDTO getMerchantDashboard(User merchant) {
        Long merchantId = merchant.getId();

        // 1. Lấy tổng doanh thu (giá gốc món ăn)
        List<Order> completedOrders = orderRepository.findCompletedOrdersByMerchant(merchantId);
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Order order : completedOrders) {
            totalRevenue = totalRevenue.add(OrderRevenueUtil.calculateOriginalRevenue(order));
        }

        // 2. Lấy tổng số đơn hoàn thành
        Long totalOrders = (long) completedOrders.size();

        // 3. Xử lý danh sách món bán chạy
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