package com.uit.fooddelivery_api.modules.restaurant.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class DashboardResponseDTO {
    private BigDecimal totalRevenue; // Tổng doanh thu
    private Long totalCompletedOrders; // Tổng số đơn thành công
    private List<FoodSaleStatDTO> bestSellers; // Top món bán chạy
    private List<FoodSaleStatDTO> worstSellers; // Top món ế nhất
}