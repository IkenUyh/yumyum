package com.uit.fooddelivery_api.modules.user.services;

import com.uit.fooddelivery_api.config.security.JwtService;
import com.uit.fooddelivery_api.modules.user.dtos.AuthResponseDTO;
import com.uit.fooddelivery_api.modules.user.dtos.UserResponseDTO;
import com.uit.fooddelivery_api.modules.user.entities.Role;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.repositories.UserRepository;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WalletRepository walletRepository;

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }

        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }

        // 1. Lưu User vào database trước để lấy ID
        User savedUser = userRepository.save(user);

        // 2. Tạo một cái ví rỗng cho User vừa tạo
        Wallet newWallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .build();

        // 3. Lưu ví vào database
        walletRepository.save(newWallet);

        return savedUser;
    }

    public AuthResponseDTO loginUser(String phoneNumber, String password) {
        // Giao cho Spring Security tự kiểm tra số điện thoại & mật khẩu
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(phoneNumber, password)
        );

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Số điện thoại không tồn tại!"));

        // Đưa thông tin user cho cỗ máy in Token
        String jwtToken = jwtService.generateToken(user);

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