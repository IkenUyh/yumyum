package com.example.uitpayapp.modules.user.models.requests;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileDTO {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    public UpdateProfileDTO(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
