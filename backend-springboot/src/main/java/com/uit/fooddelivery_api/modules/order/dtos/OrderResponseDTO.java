package com.uit.fooddelivery_api.modules.order.dtos;

import com.uit.fooddelivery_api.modules.order.entities.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderResponseDTO {
    private Long id;
    private Long restaurantId;
    private BigDecimal totalAmount;
    private String status;

    public static OrderResponseDTO fromEntity(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .restaurantId(order.getRestaurant().getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .build();
    }
}