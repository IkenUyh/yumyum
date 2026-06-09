package com.uit.fooddelivery_api.modules.user.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.user.dtos.AuthResponseDTO;
import com.uit.fooddelivery_api.modules.user.dtos.LoginRequestDTO;
import com.uit.fooddelivery_api.modules.user.dtos.UserResponseDTO;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.services.UserService;
import com.uit.fooddelivery_api.modules.user.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @PostMapping("/register")
    public ApiResponse<UserResponseDTO> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        UserResponseDTO dto = UserResponseDTO.fromEntity(savedUser);
        return ApiResponse.success(dto);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginDTO) {
        AuthResponseDTO authResponse = userService.loginUser(loginDTO.getPhoneNumber(), loginDTO.getPassword());
        return ApiResponse.success(authResponse);
    }

    // API lay thong tin dang nhap cua chinh minh tu Token
    @GetMapping("/me")
    public ApiResponse<UserResponseDTO> getProfile(Authentication authentication) {
        // Lay doi tuong User tu Security Context nho vao filter da check truoc do
        User currentUser = (User) authentication.getPrincipal();
        UserResponseDTO dto = UserResponseDTO.fromEntity(currentUser);
        return ApiResponse.success(dto);
    }

    // API upload avatar da duoc bao mat, lay thong tin user tu Token chu khong nhan vo tu parameter
    @PostMapping("/upload-avatar")
    public ApiResponse<String> uploadAvatar(
            Authentication authentication,
            @RequestParam("avatarFile") MultipartFile file) {
        try {
            // Lay phone number tu Token an toan
            String phoneNumber = authentication.getName();
            String avatarUrl = cloudinaryService.uploadAvatar(file);
            userService.updateAvatar(phoneNumber, avatarUrl);

            return ApiResponse.success(avatarUrl);
        } catch (Exception e) {
            throw new RuntimeException("Tai anh that bai: " + e.getMessage());
        }
    }

    // API: Đổi mật khẩu
    @PutMapping("/password")
    public ApiResponse<String> changePassword(
            Authentication authentication,
            @RequestBody com.uit.fooddelivery_api.modules.user.dtos.ChangePasswordDTO dto) {

        User currentUser = (User) authentication.getPrincipal();
        userService.changePassword(dto, currentUser);
        return ApiResponse.success("Đổi mật khẩu thành công!");
    }

    // API: Khách hàng tự xóa vĩnh viễn tài khoản
    @DeleteMapping("/account")
    public ApiResponse<String> deleteAccount(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        userService.deleteAccount(currentUser);
        return ApiResponse.success("Tài khoản đã được xóa vĩnh viễn khỏi hệ thống!");
    }
}