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
    private Double distance;
    private Integer deliveryTime;

    public static FavoriteRestaurantResponseDTO fromEntity(FavoriteRestaurant favorite) {
        return fromEntity(favorite, null, null);
    }

    public static FavoriteRestaurantResponseDTO fromEntity(FavoriteRestaurant favorite, Double userLat, Double userLng) {
        var r = favorite.getRestaurant();
        Double distance = null;
        Integer deliveryTime = null;

        if (userLat != null && userLng != null && r.getLatitude() != null && r.getLongitude() != null) {
            double lat1 = userLat;
            double lon1 = userLng;
            double lat2 = r.getLatitude().doubleValue();
            double lon2 = r.getLongitude().doubleValue();

            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                       Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            distance = 6371 * c; // in KM
            distance = Math.round(distance * 10.0) / 10.0;
            deliveryTime = 15 + (int) (distance * 2.4);
        }

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
                .distance(distance)
                .deliveryTime(deliveryTime)
                .build();
    }
}
