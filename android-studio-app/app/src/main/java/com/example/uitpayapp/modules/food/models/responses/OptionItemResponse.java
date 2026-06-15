package com.example.uitpayapp.modules.food.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class OptionItemResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("additionalPrice")
    private BigDecimal additionalPrice;

    @SerializedName("isAvailable")
    private Boolean isAvailable;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getAdditionalPrice() { return additionalPrice; }
    public Boolean getIsAvailable() { return isAvailable; }
}