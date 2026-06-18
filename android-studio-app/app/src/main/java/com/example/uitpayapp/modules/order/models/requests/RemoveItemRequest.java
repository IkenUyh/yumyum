package com.example.uitpayapp.modules.order.models.requests;

import com.google.gson.annotations.SerializedName;

public class RemoveItemRequest {
    @SerializedName("foodId")
    private Long foodId;

    public RemoveItemRequest(Long foodId) {
        this.foodId = foodId;
    }

    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }
}
