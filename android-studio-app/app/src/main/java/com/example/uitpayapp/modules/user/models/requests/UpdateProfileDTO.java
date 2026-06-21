package com.example.uitpayapp.modules.user.models.requests;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileDTO {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("gender")
    private String gender;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("job")
    private String job;

    @SerializedName("emailOtp")
    private String emailOtp;

    public UpdateProfileDTO(String fullName, String email, String gender, String birthday, String job) {
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.job = job;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }

    public String getEmailOtp() { return emailOtp; }
    public void setEmailOtp(String emailOtp) { this.emailOtp = emailOtp; }
}
