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
    private Double distance;
    private String note;

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
        private List<java.util.Map<String, Object>> selectedOptions;
    }

    public static OrderResponseDTO fromEntity(Order order) {
        int totalItems = 0;
        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (com.uit.fooddelivery_api.modules.order.entities.OrderItem item : order.getOrderItems()) {
                totalItems += item.getQuantity();
                
                List<java.util.Map<String, Object>> parsedOptions = null;
                if (item.getSelectedOptions() != null && !item.getSelectedOptions().isEmpty()) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        parsedOptions = mapper.readValue(item.getSelectedOptions(),
                                new com.fasterxml.jackson.core.type.TypeReference<List<java.util.Map<String, Object>>>() {
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                itemDTOs.add(OrderItemDTO.builder()
                        .name(item.getFood().getName())
                        .imageUrl(item.getFood().getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .selectedOptions(parsedOptions)
                        .build());
            }
        }

        String recipientName = null;
        String recipientPhone = null;
        if (order.getAddress() != null) {
            recipientName = order.getAddress().getRecipientName();
            recipientPhone = order.getAddress().getPhoneNumber();
        } else if (order.getUser() != null) {
            recipientName = order.getUser().getFullName();
            recipientPhone = order.getUser().getPhoneNumber();
        }

        Double calcDistance = null;
        if (order.getRestaurant() != null && order.getRestaurant().getLatitude() != null && order.getRestaurant().getLongitude() != null &&
            order.getAddress() != null && order.getAddress().getLatitude() != null && order.getAddress().getLongitude() != null) {
            calcDistance = com.uit.fooddelivery_api.common.utils.DistanceUtil.calculateDistance(
                order.getRestaurant().getLatitude().doubleValue(),
                order.getRestaurant().getLongitude().doubleValue(),
                order.getAddress().getLatitude().doubleValue(),
                order.getAddress().getLongitude().doubleValue()
            );
            calcDistance = Math.round(calcDistance * 10.0) / 10.0;
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
                .customerName(recipientName)
                .customerPhone(recipientPhone)
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
                .distance(calcDistance)
                .note(order.getNote())
                .build();
    }
}