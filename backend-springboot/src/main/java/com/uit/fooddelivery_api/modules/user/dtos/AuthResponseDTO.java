package com.uit.fooddelivery_api.modules.user.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponseDTO {
    private String token;
    private UserResponseDTO user;
}