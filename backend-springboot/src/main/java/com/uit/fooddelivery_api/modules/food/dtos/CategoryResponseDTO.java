package com.uit.fooddelivery_api.modules.food.dtos;

import com.uit.fooddelivery_api.modules.food.entities.Category;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String imageUrl;

    public static CategoryResponseDTO fromEntity(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .build();
    }
}
