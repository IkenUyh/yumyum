package com.uit.fooddelivery_api.modules.order.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPreviewResponseDTO {
    private BigDecimal foodTotal;
    private BigDecimal shippingFee;
    private BigDecimal totalOrderDiscount;
    private BigDecimal totalShippingDiscount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal finalTotal;
    private Double distanceKm;
    private LocalDateTime expectedDeliveryTime;
}
