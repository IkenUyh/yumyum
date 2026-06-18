package com.example.uitpayapp.modules.favorite.models;

import com.google.gson.annotations.SerializedName;

public class FavoriteStatusResponseDTO {
    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("isFavorited")
    private boolean isFavorited;

    public Long getRestaurantId() { return restaurantId; }
    public boolean isFavorited() { return isFavorited; }
}
