package com.uit.fooddelivery_api.modules.statistic.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueStatDTO {
    private int day;
    private BigDecimal revenue;
    private long transactionCount;
}
