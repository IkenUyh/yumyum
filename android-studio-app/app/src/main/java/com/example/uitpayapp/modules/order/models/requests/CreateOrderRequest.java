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

    @SerializedName("paymentMethod")
    private String paymentMethod;

    public CreateOrderRequest(Long restaurantId, Long addressId, String deliveryMode, List<String> voucherCodes) {
        this(restaurantId, addressId, deliveryMode, voucherCodes, null);
    }

    public CreateOrderRequest(Long restaurantId, Long addressId, String deliveryMode, List<String> voucherCodes, String paymentMethod) {
    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("addressText")
    private String addressText;

    public CreateOrderRequest(Long restaurantId, Long addressId, String deliveryMode, List<String> voucherCodes, Double latitude, Double longitude, String addressText) {
        this.restaurantId = restaurantId;
        this.addressId = addressId;
        this.deliveryMode = deliveryMode;
        this.voucherCodes = voucherCodes;
        this.paymentMethod = paymentMethod;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressText = addressText;
    }

    // Getters and Setters
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public String getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(String deliveryMode) { this.deliveryMode = deliveryMode; }

    public List<String> getVoucherCodes() { return voucherCodes; }
    public void setVoucherCodes(List<String> voucherCodes) { this.voucherCodes = voucherCodes; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAddressText() { return addressText; }
    public void setAddressText(String addressText) { this.addressText = addressText; }
}