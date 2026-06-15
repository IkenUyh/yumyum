package com.example.uitpayapp.modules.user.models.requests;

import com.google.gson.annotations.SerializedName;

public class RegisterRequestDTO {
    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("password")
    private String password;

    @SerializedName("referredByCode")
    private String referredByCode;

    public RegisterRequestDTO(String phoneNumber, String fullName, String password, String referredByCode) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.password = password;
        this.referredByCode = referredByCode;
    }
}