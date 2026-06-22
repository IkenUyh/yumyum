package com.uit.fooddelivery_api.modules.food.dtos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateOptionItemDTO {
    private String name;
    private BigDecimal additionalPrice;
    private String imageUrl;
}