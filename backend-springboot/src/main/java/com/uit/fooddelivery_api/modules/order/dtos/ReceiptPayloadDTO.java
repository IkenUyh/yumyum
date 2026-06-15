package com.uit.fooddelivery_api.modules.order.dtos;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ReceiptPayloadDTO {
    private Long orderId;
    private String customerName;
    private BigDecimal totalAmount;
    private String printTime;
}