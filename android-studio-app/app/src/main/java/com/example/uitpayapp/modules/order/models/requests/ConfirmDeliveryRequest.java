package com.example.uitpayapp.modules.order.models.requests;

import com.google.gson.annotations.SerializedName;

public class ConfirmDeliveryRequest {
    @SerializedName("deliveryPin")
    private String deliveryPin;

    public ConfirmDeliveryRequest(String deliveryPin) {
        this.deliveryPin = deliveryPin;
    }

    public String getDeliveryPin() { return deliveryPin; }
    public void setDeliveryPin(String deliveryPin) { this.deliveryPin = deliveryPin; }
}