package com.uit.fooddelivery_api.modules.order.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderDTO {
    private Long restaurantId;
    private Long addressId; // Bổ sung ID địa chỉ giao hàng
    private String voucherCode;
}