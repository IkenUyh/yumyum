package com.uit.fooddelivery_api.modules.user.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String phoneNumber;
    private String password;
}