package com.example.uitpayapp.modules.merchant.models.responses;

import com.google.gson.annotations.SerializedName;

public class MerchantRequestResponseDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("storeName")
    private String storeName;

    @SerializedName("storeAddress")
    private String storeAddress;

    @SerializedName("storePhone")
    private String storePhone;

    @SerializedName("businessLicenseUrl")
    private String businessLicenseUrl;

    @SerializedName("status")
    private String status;

    @SerializedName("confirmationCode")
    private String confirmationCode;

    @SerializedName("createdAt")
    private String createdAt; // Ánh xạ từ LocalDateTime sang dạng String để dễ format ở Client

    @SerializedName("ownerName")
    private String ownerName;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    // Getters and Setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getStoreName() { return storeName; }
    public String getStoreAddress() { return storeAddress; }
    public String getStorePhone() { return storePhone; }
    public String getBusinessLicenseUrl() { return businessLicenseUrl; }
    public String getStatus() { return status; }
    public String getConfirmationCode() { return confirmationCode; }
    public String getCreatedAt() { return createdAt; }
    public String getOwnerName() { return ownerName; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}