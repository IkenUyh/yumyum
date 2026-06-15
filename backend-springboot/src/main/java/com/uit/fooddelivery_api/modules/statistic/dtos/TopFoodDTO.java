package com.uit.fooddelivery_api.modules.statistic.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopFoodDTO {
    private Long foodId;
    private String foodName;
    private Long totalSold; // Tổng số phần đã bán ra
}