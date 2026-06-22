package com.uit.fooddelivery_api.modules.restaurant.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FoodSaleStatDTO {
    private Long foodId;
    private String foodName;
    private Long totalSold;
}