package com.uit.fooddelivery_api.modules.review.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewDTO {
    private Long orderId;
    private Integer rating; // Từ 1 đến 5 sao
    private String comment;
}