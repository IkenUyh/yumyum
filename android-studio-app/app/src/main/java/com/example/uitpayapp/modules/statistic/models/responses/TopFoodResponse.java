package com.example.uitpayapp.modules.statistic.models.responses;

import com.google.gson.annotations.SerializedName;

public class TopFoodResponse {
    @SerializedName("foodId")
    private Long foodId;

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("totalSold")
    private Long totalSold;

    // Constructors
    public TopFoodResponse() {}

    public TopFoodResponse(Long foodId, String foodName, Long totalSold) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.totalSold = totalSold;
    }

    // Getters and Setters
    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public Long getTotalSold() { return totalSold; }
    public void setTotalSold(Long totalSold) { this.totalSold = totalSold; }
}