package com.uit.fooddelivery_api.modules.food.dtos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateFoodDTO {
    private Long restaurantId;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
}