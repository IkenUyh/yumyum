package com.example.uitpayapp.modules.food.models.requests;

import com.google.gson.annotations.SerializedName;

public class CreateCategoryRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("restaurantId")
    private Long restaurantId;

    public CreateCategoryRequest(String name, Long restaurantId) {
        this.name = name;
        this.restaurantId = restaurantId;
    }

    // Getters và Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
}
