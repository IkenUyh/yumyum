package com.uit.fooddelivery_api.modules.user.dtos;

import com.uit.fooddelivery_api.modules.user.entities.User;
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
    private String email;
    private String avatarUrl;
    private String role;
    private String gender;
    private String birthday;
    private String job;

    // Viết hàm tiện ích để chuyển từ Entity gốc sang DTO an toàn
    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .job(user.getJob())
                .build();
    }
}