package com.uit.fooddelivery_api.modules.cart.dtos;

import com.uit.fooddelivery_api.modules.cart.entities.CartItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CartItemResponseDTO {
    private Long id;
    private Long foodId;
    private String foodName;
    private String foodImageUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal itemTotal; // Tổng tiền = price * quantity

    public static CartItemResponseDTO fromEntity(CartItem cartItem) {
        BigDecimal currentPrice = cartItem.getFood().getPrice();
        Integer qty = cartItem.getQuantity();

        return CartItemResponseDTO.builder()
                .id(cartItem.getId())
                .foodId(cartItem.getFood().getId())
                .foodName(cartItem.getFood().getName())
                .foodImageUrl(cartItem.getFood().getImageUrl())
                .price(currentPrice)
                .quantity(qty)
                .itemTotal(currentPrice.multiply(BigDecimal.valueOf(qty)))
                .build();
    }
}