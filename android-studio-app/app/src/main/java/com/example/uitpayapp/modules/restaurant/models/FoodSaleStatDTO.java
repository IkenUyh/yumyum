package com.example.uitpayapp.modules.restaurant.models;

import com.google.gson.annotations.SerializedName;

public class FoodSaleStatDTO {
    @SerializedName("foodId")
    private Long foodId;

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("totalSold")
    private Long totalSold;

    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public Long getTotalSold() { return totalSold; }
    public void setTotalSold(Long totalSold) { this.totalSold = totalSold; }
}
