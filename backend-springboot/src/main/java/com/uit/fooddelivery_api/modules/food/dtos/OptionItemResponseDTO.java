package com.uit.fooddelivery_api.modules.food.dtos;

import com.uit.fooddelivery_api.modules.food.entities.FoodOptionItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OptionItemResponseDTO {
    private Long id;
    private String name;
    private BigDecimal additionalPrice;
    private String imageUrl;
    private Boolean isAvailable;

    public static OptionItemResponseDTO fromEntity(FoodOptionItem item) {
        return OptionItemResponseDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .additionalPrice(item.getAdditionalPrice())
                .imageUrl(item.getImageUrl())
                .isAvailable(item.getIsAvailable())
                .build();
    }
}