package com.uit.fooddelivery_api.modules.statistic.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class MerchantDashboardDTO {
    private BigDecimal totalRevenue; // Tổng doanh thu
    private Long totalCompletedOrders; // Tổng số đơn thành công
    private List<TopFoodDTO> topSellingFoods; // Bảng xếp hạng món ăn
}