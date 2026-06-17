package com.uit.fooddelivery_api.modules.favorite.dtos;

import com.uit.fooddelivery_api.modules.favorite.entities.FavoriteRestaurant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class FavoriteRestaurantResponseDTO {

    private Long favoriteId;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantImageUrl;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isAcceptingOrders;
    private java.math.BigDecimal ratingAverage;
    private Integer reviewCount;
    private LocalDateTime savedAt;

    public static FavoriteRestaurantResponseDTO fromEntity(FavoriteRestaurant favorite) {
        var r = favorite.getRestaurant();
        return FavoriteRestaurantResponseDTO.builder()
                .favoriteId(favorite.getId())
                .restaurantId(r.getId())
                .restaurantName(r.getName())
                .restaurantAddress(r.getAddress())
                .restaurantImageUrl(r.getImageUrl())
                .openTime(r.getOpenTime())
                .closeTime(r.getCloseTime())
                .isAcceptingOrders(r.getIsAcceptingOrders())
                .ratingAverage(r.getRatingAverage())
                .reviewCount(r.getReviewCount())
                .savedAt(favorite.getCreatedAt())
                .build();
    }
}
