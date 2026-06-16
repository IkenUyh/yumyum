package com.example.uitpayapp.modules.order.models.requests;

import com.google.gson.annotations.SerializedName;

public class ConfirmPickupRequest {
    @SerializedName("pickupCode")
    private String pickupCode;

    public ConfirmPickupRequest(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public String getPickupCode() { return pickupCode; }
    public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }
}