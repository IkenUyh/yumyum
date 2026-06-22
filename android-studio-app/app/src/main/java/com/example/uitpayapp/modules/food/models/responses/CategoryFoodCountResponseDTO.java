package com.example.uitpayapp.modules.food.models.responses;

import com.google.gson.annotations.SerializedName;

public class CategoryFoodCountResponseDTO {
    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName(value = "categoryName", alternate = {"name"})
    private String categoryName;

    @SerializedName("foodCount")
    private int foodCount;

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getFoodCount() {
        return foodCount;
    }
}
