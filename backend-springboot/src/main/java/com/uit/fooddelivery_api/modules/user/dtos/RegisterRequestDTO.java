package com.uit.fooddelivery_api.modules.user.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String phoneNumber;
    private String fullName;
    private String email;
    private String password;
    private String referredByCode; // Nhận mã giới thiệu từ Frontend (Có thể null)
}