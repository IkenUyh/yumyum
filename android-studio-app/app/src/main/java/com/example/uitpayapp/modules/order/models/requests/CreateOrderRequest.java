package com.example.uitpayapp.modules.order.models.requests;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateOrderRequest {
    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("addressId")
    private Long addressId;

    @SerializedName("deliveryMode")
    private String deliveryMode; // STANDARD, FAST, EXPRESS

    @SerializedName("voucherCodes")
    private List<String> voucherCodes;

    public CreateOrderRequest(Long restaurantId, Long addressId, String deliveryMode, List<String> voucherCodes) {
        this.restaurantId = restaurantId;
        this.addressId = addressId;
        this.deliveryMode = deliveryMode;
        this.voucherCodes = voucherCodes;
    }

    // Getters and Setters
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public String getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(String deliveryMode) { this.deliveryMode = deliveryMode; }

    public List<String> getVoucherCodes() { return voucherCodes; }
    public void setVoucherCodes(List<String> voucherCodes) { this.voucherCodes = voucherCodes; }
}