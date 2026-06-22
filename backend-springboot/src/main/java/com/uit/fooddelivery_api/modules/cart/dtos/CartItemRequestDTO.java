package com.uit.fooddelivery_api.modules.cart.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartItemRequestDTO {
    private Long foodId;
    private Integer quantity; // Số lượng muốn thêm (hoặc cập nhật)
    private List<Long> selectedOptionItemIds;
    private String appliedPromotion;
}