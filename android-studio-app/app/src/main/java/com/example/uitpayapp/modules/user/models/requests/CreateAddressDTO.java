package com.example.uitpayapp.modules.user.models.requests;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CreateAddressDTO {
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

    public CreateAddressDTO(String addressName, String recipientName, String phoneNumber,
                            String detailedAddress, BigDecimal latitude, BigDecimal longitude, Boolean isDefault) {
        this.addressName = addressName;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.detailedAddress = detailedAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault;
    }
}