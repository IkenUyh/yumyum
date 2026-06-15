package com.example.uitpayapp.modules.food.models.requests;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CreateOptionItemRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("additionalPrice")
    private BigDecimal additionalPrice;

    public CreateOptionItemRequest(String name, BigDecimal additionalPrice) {
        this.name = name;
        this.additionalPrice = additionalPrice;
    }

    // Getters và Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getAdditionalPrice() { return additionalPrice; }
    public void setAdditionalPrice(BigDecimal additionalPrice) { this.additionalPrice = additionalPrice; }
}