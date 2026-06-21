package com.uit.fooddelivery_api.modules.food.dtos;

import com.uit.fooddelivery_api.modules.food.entities.Food;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class FoodResponseDTO {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isAvailable;

    public static FoodResponseDTO fromEntity(Food food) {
        return FoodResponseDTO.builder()
                .id(food.getId())
                .restaurantId(food.getRestaurant().getId())
                .restaurantName(food.getRestaurant().getName())
                .categoryId(food.getCategory() != null ? food.getCategory().getId() : null)
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .isAvailable(food.getIsAvailable())
                .build();
    }
}