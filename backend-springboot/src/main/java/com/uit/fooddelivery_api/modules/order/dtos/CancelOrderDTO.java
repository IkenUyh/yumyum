package com.uit.fooddelivery_api.modules.order.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrderDTO {
    private String reason; // Lý do hủy đơn
}