package com.uit.fooddelivery_api.modules.order.dtos;
 
import com.uit.fooddelivery_api.modules.order.entities.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantImageUrl;
    private BigDecimal totalAmount;
    private String status;
    private String deliveryMode;
    private java.time.LocalDateTime expectedDeliveryTime;
    private java.time.LocalDateTime createdAt;
    private Integer itemCount;
    private List<OrderItemDTO> items;
    private String customerName;
    private String customerPhone;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private String name;
        private String imageUrl;
        private Integer quantity;
        private BigDecimal price;
    }

    public static OrderResponseDTO fromEntity(Order order) {
        int totalItems = 0;
        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (com.uit.fooddelivery_api.modules.order.entities.OrderItem item : order.getOrderItems()) {
                totalItems += item.getQuantity();
                itemDTOs.add(OrderItemDTO.builder()
                        .name(item.getFood().getName())
                        .imageUrl(item.getFood().getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build());
            }
        }

        return OrderResponseDTO.builder()
                .id(order.getId())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .restaurantImageUrl(order.getRestaurant().getImageUrl())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .deliveryMode(order.getDeliveryMode())
                .expectedDeliveryTime(order.getExpectedDeliveryTime())
                .createdAt(order.getCreatedAt())
                .itemCount(totalItems)
                .items(itemDTOs)
                .customerName(order.getUser() != null ? order.getUser().getFullName() : null)
                .customerPhone(order.getUser() != null ? order.getUser().getPhoneNumber() : null)
                .build();
    }
}