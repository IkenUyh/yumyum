package com.example.uitpayapp.modules.user.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class AddressResponseDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("addressName")
    private String addressName;

    @SerializedName("recipientName")
    private String recipientName;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("detailedAddress")
    private String detailedAddress;

    @SerializedName("latitude")
    private BigDecimal latitude;

    @SerializedName("longitude")
    private BigDecimal longitude;

    @SerializedName("isDefault")
    private Boolean isDefault;

    // Getters
    public Long getId() { return id; }
    public String getAddressName() { return addressName; }
    public String getRecipientName() { return recipientName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getDetailedAddress() { return detailedAddress; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public Boolean getIsDefault() { return isDefault; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setAddressName(String addressName) { this.addressName = addressName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDetailedAddress(String detailedAddress) { this.detailedAddress = detailedAddress; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}