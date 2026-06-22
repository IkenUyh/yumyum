package com.example.uitpayapp.modules.user.models.responses;
import com.google.gson.annotations.SerializedName;

public class AuthResponseDTO {
    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private UserResponseDTO user;

    public String getToken() { return token; }
    public UserResponseDTO getUser() { return user; }
}