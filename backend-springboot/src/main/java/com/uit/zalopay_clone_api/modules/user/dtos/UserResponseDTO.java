package com.uit.zalopay_clone_api.modules.user.dtos;

import com.uit.zalopay_clone_api.modules.user.entities.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String phoneNumber;
    private String fullName;

    // Viết hàm tiện ích để chuyển từ Entity gốc sang DTO an toàn
    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .build();
    }
}