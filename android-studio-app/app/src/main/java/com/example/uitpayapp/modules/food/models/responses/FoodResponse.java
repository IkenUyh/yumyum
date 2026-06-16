package com.example.uitpayapp.modules.food.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class FoodResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("isAvailable")
    private Boolean isAvailable;

    // Getters
    public Long getId() { return id; }
    public Long getRestaurantId() { return restaurantId; }
    public Long getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public Boolean getIsAvailable() { return isAvailable; }
}