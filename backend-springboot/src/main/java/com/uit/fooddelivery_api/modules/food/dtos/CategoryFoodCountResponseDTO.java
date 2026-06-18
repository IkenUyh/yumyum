package com.uit.fooddelivery_api.modules.food.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFoodCountResponseDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Long foodCount;
}
