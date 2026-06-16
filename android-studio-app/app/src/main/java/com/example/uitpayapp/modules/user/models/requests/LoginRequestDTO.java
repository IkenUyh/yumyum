package com.example.uitpayapp.modules.user.models.requests;

import com.google.gson.annotations.SerializedName;

public class LoginRequestDTO {
    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    public LoginRequestDTO(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}