package com.uit.fooddelivery_api.modules.user.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileDTO {
    private String fullName;
    private String email;
    private String gender;
    private String birthday;
    private String job;
}
