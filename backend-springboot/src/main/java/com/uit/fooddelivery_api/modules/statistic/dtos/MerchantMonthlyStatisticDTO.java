package com.uit.fooddelivery_api.modules.statistic.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class MerchantMonthlyStatisticDTO {
    private int month;
    private int year;
    private BigDecimal totalRevenue;
    private BigDecimal avgDailyRevenue;
    private BigDecimal highestDailyRevenue;
    private BigDecimal lowestDailyRevenue;
    private long totalTransactions;
    private List<DailyRevenueStatDTO> dailyStats;
}
