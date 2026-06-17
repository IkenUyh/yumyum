package com.example.uitpayapp.modules.user.models.requests;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequestDTO {
    @SerializedName("email")
    private String email;

    public ForgotPasswordRequestDTO(String email) {
        this.email = email;
    }
}
