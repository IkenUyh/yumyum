package com.uit.fooddelivery_api.modules.order.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderDTO {
    private Long restaurantId;
    private Long addressId; //ID địa chỉ giao hàng
    private String deliveryMode; //Khách truyền STANDARD, FAST, hoặc EXPRESS
    private List<String> voucherCodes;
}