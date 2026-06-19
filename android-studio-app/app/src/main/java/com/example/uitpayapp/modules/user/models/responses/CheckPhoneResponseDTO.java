package com.example.uitpayapp.modules.user.models.responses;

import com.google.gson.annotations.SerializedName;

public class CheckPhoneResponseDTO {
    @SerializedName("exists")
    private boolean exists;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("fullName")
    private String fullName;

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
