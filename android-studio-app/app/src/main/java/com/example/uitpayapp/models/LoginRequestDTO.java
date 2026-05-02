package com.example.uitpayapp.models;

public class LoginRequestDTO {
    private String phoneNumber;
    private String password;

    public LoginRequestDTO(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}