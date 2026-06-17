package com.example.uitpayapp.modules.user.models.requests;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequestDTO {
    @SerializedName("email")
    private String email;

    @SerializedName("otp")
    private String otp;

    @SerializedName("newPassword")
    private String newPassword;

    @SerializedName("confirmPassword")
    private String confirmPassword;

    public ResetPasswordRequestDTO(String email, String otp, String newPassword, String confirmPassword) {
        this.email = email;
        this.otp = otp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
