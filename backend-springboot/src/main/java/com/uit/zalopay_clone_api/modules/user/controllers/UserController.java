package com.uit.zalopay_clone_api.modules.user.controllers;

import com.uit.zalopay_clone_api.common.responses.ApiResponse;
import com.uit.zalopay_clone_api.modules.user.dtos.LoginRequestDTO;
import com.uit.zalopay_clone_api.modules.user.dtos.UserResponseDTO;
import com.uit.zalopay_clone_api.modules.user.entities.User;
import com.uit.zalopay_clone_api.modules.user.services.UserService;
import com.uit.zalopay_clone_api.modules.user.services.CloudinaryService; // Thêm thư viện này
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*; // Gộp import
import org.springframework.web.multipart.MultipartFile; // Thêm thư viện này

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService; // Lombok sẽ tự lo việc Inject cỗ máy Cloudinary

    @PostMapping("/register")
    public ApiResponse<UserResponseDTO> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        UserResponseDTO dto = UserResponseDTO.fromEntity(savedUser);
        return ApiResponse.success(dto);
    }

    @PostMapping("/login")
    public ApiResponse<UserResponseDTO> login(@RequestBody LoginRequestDTO loginDTO) {
        // Gọi Service xử lý đăng nhập
        User loggedInUser = userService.loginUser(loginDTO.getPhoneNumber(), loginDTO.getPassword());

        // Chuyển sang DTO (để giấu pass) và bọc ApiResponse lại
        UserResponseDTO responseDTO = UserResponseDTO.fromEntity(loggedInUser);
        return ApiResponse.success(responseDTO);
    }

    @PostMapping("/upload-avatar")
    public ApiResponse<String> uploadAvatar(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("avatarFile") MultipartFile file) {
        try {
            String avatarUrl = cloudinaryService.uploadAvatar(file);
            userService.updateAvatar(phoneNumber, avatarUrl);

            return ApiResponse.success(avatarUrl);

        }
        catch (Exception e) {
            throw new RuntimeException("Tải ảnh thất bại: " + e.getMessage());
        }
    }
}