package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedDealDTO {
    private String storeName;
    private double distance;
    private int deliveryTime;
    private int foodImageResId;
    private String imageUrl;
    private String discountTag;
    private String foodTitle;
    private long soldCount;
    private double originalPrice;
    private double discountPrice;
    private double rating;
    private Long restaurantId;
}
