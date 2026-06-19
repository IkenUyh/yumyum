package com.uit.fooddelivery_api.modules.restaurant.dtos;

import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class RestaurantResponseDTO {
    private Long id;
    private String name;
    private String address;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Long merchantId;
    private Boolean isAcceptingOrders;
    private Integer maxPendingOrders;
    private String imageUrl;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;

    // Ham tien ich de chuyen tu Entity sang DTO
    public static RestaurantResponseDTO fromEntity(Restaurant restaurant) {
        return RestaurantResponseDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .openTime(restaurant.getOpenTime())
                .closeTime(restaurant.getCloseTime())
                .merchantId(restaurant.getMerchant().getId())
                .isAcceptingOrders(restaurant.getIsAcceptingOrders())
                .maxPendingOrders(restaurant.getMaxPendingOrders())
                .imageUrl(restaurant.getImageUrl())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .build();
    }
}