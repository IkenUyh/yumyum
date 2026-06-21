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
    private Boolean reviewed;
    private Boolean reviewExpired;
    private java.math.BigDecimal shippingFee;
    private java.math.BigDecimal discountAmount;
    private String destAddress;
    private java.math.BigDecimal restaurantLatitude;
    private java.math.BigDecimal restaurantLongitude;
    private java.math.BigDecimal destLatitude;
    private java.math.BigDecimal destLongitude;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentUrl;

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
                .reviewed(order.getReview() != null)
                .reviewExpired(order.getCreatedAt() != null && java.time.LocalDateTime.now().isAfter(order.getCreatedAt().plusDays(7)))
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .destAddress(order.getAddress() != null ? order.getAddress().getDetailedAddress() : null)
                .restaurantLatitude(order.getRestaurant().getLatitude())
                .restaurantLongitude(order.getRestaurant().getLongitude())
                .destLatitude(order.getAddress() != null ? order.getAddress().getLatitude() : null)
                .destLongitude(order.getAddress() != null ? order.getAddress().getLongitude() : null)
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paymentUrl(order.getPaymentUrl())
                .build();
    }
}