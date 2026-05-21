package com.uit.fooddelivery_api.modules.user.services;

import com.uit.fooddelivery_api.config.security.JwtService;
import com.uit.fooddelivery_api.modules.user.dtos.AuthResponseDTO;
import com.uit.fooddelivery_api.modules.user.dtos.UserResponseDTO;
import com.uit.fooddelivery_api.modules.user.entities.Role;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // Tiêm thêm 2 thằng này vào để xử lý Token và Login
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public User registerUser(User user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }

        // Gán Role mặc định là Khách hàng nếu chưa có
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }

        return userRepository.save(user);
    }

    public AuthResponseDTO loginUser(String phoneNumber, String password) {
        // 1. Giao cho Spring Security tự kiểm tra số điện thoại & mật khẩu
        // Nếu sai nó sẽ tự ném lỗi (BadCredentialsException)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(phoneNumber, password)
        );

        // 2. Nếu code chạy được xuống đây tức là password chuẩn, kéo user từ DB ra
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Số điện thoại không tồn tại!"));

        // 3. Đưa thông tin user cho cỗ máy in Token
        String jwtToken = jwtService.generateToken(user);

        // 4. Bọc lại thành AuthResponseDTO và trả về
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .user(UserResponseDTO.fromEntity(user))
                .build();
    }

    public void updateAvatar(String phoneNumber, String avatarUrl) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }
}