package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantHomeDTO {
    private String name;
    private String shortName;
    private int bgColor;
    private String category;
    private List<FoodMenuItemDTO> menu;
    private int imageResId;
    private double rating;
    private int reviewCount;
    private int deliveryTime;
    private String address;
}
