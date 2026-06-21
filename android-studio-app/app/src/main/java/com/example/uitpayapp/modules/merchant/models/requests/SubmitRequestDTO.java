package com.example.uitpayapp.modules.merchant.models.requests;

import com.google.gson.annotations.SerializedName;

public class SubmitRequestDTO {
    @SerializedName("storeName")
    private String storeName;

    @SerializedName("storeAddress")
    private String storeAddress;

    @SerializedName("storePhone")
    private String storePhone;

    @SerializedName("confirmationCode")
    private String confirmationCode;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    public SubmitRequestDTO(String storeName, String storeAddress, String storePhone, String confirmationCode, Double latitude, Double longitude) {
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storePhone = storePhone;
        this.confirmationCode = confirmationCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }

    public String getStorePhone() { return storePhone; }
    public void setStorePhone(String storePhone) { this.storePhone = storePhone; }

    public String getConfirmationCode() { return confirmationCode; }
    public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}