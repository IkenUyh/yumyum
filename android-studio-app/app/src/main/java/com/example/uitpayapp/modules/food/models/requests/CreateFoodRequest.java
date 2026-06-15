package com.example.uitpayapp.modules.food.models.requests;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CreateFoodRequest {
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

    public CreateFoodRequest(Long restaurantId, Long categoryId, String name, String description, BigDecimal price) {
        this.restaurantId = restaurantId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters và Setters
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}