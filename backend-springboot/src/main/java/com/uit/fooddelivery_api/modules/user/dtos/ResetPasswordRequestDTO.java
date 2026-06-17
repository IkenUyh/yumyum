package com.uit.fooddelivery_api.modules.user.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}
