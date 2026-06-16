package com.example.uitpayapp.modules.restaurant.models;

import com.google.gson.annotations.SerializedName;

public class RestaurantSettingsDTO {
    @SerializedName("isAcceptingOrders")
    private Boolean isAcceptingOrders;

    @SerializedName("maxPendingOrders")
    private Integer maxPendingOrders;

    public RestaurantSettingsDTO(Boolean isAcceptingOrders, Integer maxPendingOrders) {
        this.isAcceptingOrders = isAcceptingOrders;
        this.maxPendingOrders = maxPendingOrders;
    }

    public Boolean getIsAcceptingOrders() { return isAcceptingOrders; }
    public void setIsAcceptingOrders(Boolean isAcceptingOrders) { this.isAcceptingOrders = isAcceptingOrders; }
    public Integer getMaxPendingOrders() { return maxPendingOrders; }
    public void setMaxPendingOrders(Integer maxPendingOrders) { this.maxPendingOrders = maxPendingOrders; }
}
