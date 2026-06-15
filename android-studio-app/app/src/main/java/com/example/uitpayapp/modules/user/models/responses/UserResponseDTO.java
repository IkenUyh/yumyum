package com.example.uitpayapp.modules.user.models.responses;

import com.google.gson.annotations.SerializedName;

public class UserResponseDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    public Long getId() { return id; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getFullName() { return fullName; }
    public String getAvatarUrl() { return avatarUrl; }
}