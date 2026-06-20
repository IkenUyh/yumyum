package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodMenuItemDTO {
    private String id;
    private String name;
    private long price;
    private int imageResId;
    private String imageUrl;
    private String description;
    private Long restaurantId;
}
