package com.uit.fooddelivery_api.modules.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckPhoneResponseDTO {
    private boolean exists;
    private String avatarUrl;
    private String fullName;
}
