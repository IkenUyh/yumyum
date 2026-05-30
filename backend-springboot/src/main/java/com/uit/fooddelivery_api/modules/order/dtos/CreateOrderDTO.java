package com.uit.fooddelivery_api.modules.order.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CreateOrderDTO {
    private Long restaurantId;
    private List<CartItemDTO> items;
}