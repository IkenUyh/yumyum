package com.example.uitpayapp.modules.user.models.responses;

import com.google.gson.annotations.SerializedName;

public class UserResponseDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("role")
    private String role;

    @SerializedName("gender")
    private String gender;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("job")
    private String job;

    public Long getId() { return id; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getRole() { return role; }
    public String getGender() { return gender; }
    public String getBirthday() { return birthday; }
    public String getJob() { return job; }
}