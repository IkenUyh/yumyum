package com.uit.fooddelivery_api.modules.food.dtos;

import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.entities.FoodOptionGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class FoodDetailResponseDTO {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String discountType;
    private String imageUrl;
    private Boolean isAvailable;
    private List<OptionGroupResponseDTO> optionGroups;

    public static FoodDetailResponseDTO fromEntity(Food food, List<FoodOptionGroup> optionGroups) {
        return FoodDetailResponseDTO.builder()
                .id(food.getId())
                .restaurantId(food.getRestaurant() != null ? food.getRestaurant().getId() : null)
                .restaurantName(food.getRestaurant() != null ? food.getRestaurant().getName() : null)
                .categoryId(food.getCategory() != null ? food.getCategory().getId() : null)
                .categoryName(food.getCategory() != null ? food.getCategory().getName() : null)
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .originalPrice(food.getPrice()) // default
                .discountType(null) // default
                .imageUrl(food.getImageUrl())
                .isAvailable(food.getIsAvailable())
                .optionGroups(optionGroups != null ? 
                        optionGroups.stream().map(OptionGroupResponseDTO::fromEntity).toList() : 
                        List.of())
                .build();
    }
}
