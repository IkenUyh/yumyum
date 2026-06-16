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
}