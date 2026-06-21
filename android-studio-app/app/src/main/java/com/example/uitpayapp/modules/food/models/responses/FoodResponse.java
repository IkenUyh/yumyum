package com.example.uitpayapp.modules.food.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class FoodResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("restaurantName")
    private String restaurantName;

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

    @SerializedName("distance")
    private Double distance;

    // Getters
    public Long getId() { return id; }
    public Long getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public Long getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public void setId(Long id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
}