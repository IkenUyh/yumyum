package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodCategoryDTO {
    private String name;
    private String emoji;
    private int iconResId;
    private int bgColor;
    private boolean isSelectAll;
}
