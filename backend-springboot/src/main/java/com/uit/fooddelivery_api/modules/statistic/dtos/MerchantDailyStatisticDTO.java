package com.uit.fooddelivery_api.modules.statistic.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class MerchantDailyStatisticDTO {
    private BigDecimal totalRevenue;
    private Long transactionCount;
}
