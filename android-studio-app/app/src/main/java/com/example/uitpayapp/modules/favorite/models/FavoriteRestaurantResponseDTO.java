package com.example.uitpayapp.modules.favorite.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class FavoriteRestaurantResponseDTO {
    @SerializedName("favoriteId")
    private Long favoriteId;

    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("restaurantName")
    private String restaurantName;

    @SerializedName("restaurantAddress")
    private String restaurantAddress;

    @SerializedName("restaurantImageUrl")
    private String restaurantImageUrl;

    @SerializedName("openTime")
    private String openTime;

    @SerializedName("closeTime")
    private String closeTime;

    @SerializedName("isAcceptingOrders")
    private Boolean isAcceptingOrders;

    @SerializedName("ratingAverage")
    private BigDecimal ratingAverage;

    @SerializedName("reviewCount")
    private Integer reviewCount;

    @SerializedName("savedAt")
    private String savedAt;

    @SerializedName("distance")
    private Double distance;

    @SerializedName("deliveryTime")
    private Integer deliveryTime;

    public Long getFavoriteId() { return favoriteId; }
    public Long getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public String getRestaurantAddress() { return restaurantAddress; }
    public String getRestaurantImageUrl() { return restaurantImageUrl; }
    public String getOpenTime() { return openTime; }
    public String getCloseTime() { return closeTime; }
    public Boolean getIsAcceptingOrders() { return isAcceptingOrders; }
    public BigDecimal getRatingAverage() { return ratingAverage; }
    public Integer getReviewCount() { return reviewCount; }
    public String getSavedAt() { return savedAt; }
    public Double getDistance() { return distance; }
    public Integer getDeliveryTime() { return deliveryTime; }
}
