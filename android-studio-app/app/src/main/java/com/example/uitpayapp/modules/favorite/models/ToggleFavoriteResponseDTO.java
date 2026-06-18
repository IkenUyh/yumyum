package com.example.uitpayapp.modules.favorite.models;

import com.google.gson.annotations.SerializedName;

public class ToggleFavoriteResponseDTO {
    @SerializedName("isFavorited")
    private boolean isFavorited;

    @SerializedName("message")
    private String message;

    @SerializedName("restaurantId")
    private Long restaurantId;

    public boolean isFavorited() { return isFavorited; }
    public String getMessage() { return message; }
    public Long getRestaurantId() { return restaurantId; }
}
