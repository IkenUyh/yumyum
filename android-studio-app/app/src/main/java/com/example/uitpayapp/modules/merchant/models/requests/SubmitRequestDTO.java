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

    public SubmitRequestDTO(String storeName, String storeAddress, String storePhone, String confirmationCode) {
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storePhone = storePhone;
        this.confirmationCode = confirmationCode;
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
}