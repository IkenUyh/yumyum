package com.uit.fooddelivery_api.modules.user.services;

import com.uit.fooddelivery_api.config.security.JwtService;
import com.uit.fooddelivery_api.modules.user.dtos.AuthResponseDTO;
import com.uit.fooddelivery_api.modules.user.dtos.UserResponseDTO;
import com.uit.fooddelivery_api.modules.user.dtos.UpdateProfileDTO;
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
    private final com.uit.fooddelivery_api.modules.notification.services.NotificationService notificationService;
    private final EmailService emailService;
    private final org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;

    @Transactional
    public User registerUser(com.uit.fooddelivery_api.modules.user.dtos.RegisterRequestDTO dto) {
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(dto.getEmail().trim())) {
                throw new RuntimeException("Email này đã được đăng ký!");
            }
        }

        // 1. Tạo User mới và sinh mã giới thiệu ngẫu nhiên
        String myReferralCode = "FD" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        User user = User.builder()
                .phoneNumber(dto.getPhoneNumber())
                .fullName(dto.getFullName())
                .email(dto.getEmail() != null ? dto.getEmail().trim() : null)
                .password(dto.getPassword()) // Trong thực tế nhớ mã hóa Bcrypt nhé
                .role(Role.CUSTOMER)
                .isActive(true)
                .referralCode(myReferralCode)
                .build();

        User savedUser = userRepository.save(user);

        // 2. Tạo Ví (Wallet) rỗng cho User mới
        Wallet newWallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .build();

        // ==========================================
        // 3. LOGIC TẶNG THƯỞNG REFERRAL (ISSUE #19)
        // ==========================================
        if (dto.getReferredByCode() != null && !dto.getReferredByCode().trim().isEmpty()) {
            java.util.Optional<User> referrerOpt = userRepository.findByReferralCode(dto.getReferredByCode());

            if (referrerOpt.isPresent()) {
                User referrer = referrerOpt.get();
                savedUser.setReferredById(referrer.getId()); // Lưu vết người giới thiệu

                // A. Thưởng cho người đi mời (Cộng 20k)
                Wallet referrerWallet = walletRepository.findByUserId(referrer.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ví người giới thiệu"));
                referrerWallet.setBalance(referrerWallet.getBalance().add(BigDecimal.valueOf(20000)));
                walletRepository.save(referrerWallet);

                // Bắn thông báo Real-time cho người mời
                notificationService.pushNotification(
                        referrer.getId(),
                        "Thưởng giới thiệu bạn bè \uD83C\uDF89",
                        "Bạn vừa nhận được 20.000đ vào ví vì đã giới thiệu thành công bạn: " + savedUser.getFullName(),
                        "PROMOTION"
                );

                // B. Thưởng cho người mới tải app (Cộng 10k làm vốn)
                newWallet.setBalance(BigDecimal.valueOf(10000));
            } else {
                throw new RuntimeException("Mã giới thiệu không hợp lệ hoặc không tồn tại!");
            }
        }

        walletRepository.save(newWallet);
        return userRepository.save(savedUser); // Cập nhật lại referredById
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

    @Transactional
    public User updateProfile(String phoneNumber, UpdateProfileDTO dto) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            user.setFullName(dto.getFullName().trim());
        }

        if (dto.getEmail() != null) {
            String email = dto.getEmail().trim();
            if (email.isEmpty()) {
                user.setEmail(null);
            } else {
                java.util.Optional<User> existingUser = userRepository.findByEmail(email);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                    throw new RuntimeException("Email này đã được đăng ký bởi người dùng khác!");
                }
                user.setEmail(email);
            }
        }

        return userRepository.save(user);
    }

    // ĐỔI MẬT KHẨU
    @Transactional
    public void changePassword(com.uit.fooddelivery_api.modules.user.dtos.ChangePasswordDTO dto, User user) {
        // Kiểm tra mật khẩu cũ
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }

        // Kiểm tra xác nhận mật khẩu mới
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        user.setPassword(dto.getNewPassword());
        userRepository.save(user);
    }

    // XÓA TÀI KHOẢN (SOFT DELETE + ANONYMIZATION)
    @Transactional
    public void deleteAccount(User user) {
        // 1. Khóa tài khoản
        user.setIsActive(false);

        // 2. Ẩn danh dữ liệu cá nhân (Chuẩn Privacy / GDPR)
        user.setFullName("Người dùng đã xóa");
        user.setAvatarUrl(null);

        // Đổi mật khẩu thành rác để không ai dò được
        user.setPassword("DELETED_" + System.currentTimeMillis());

        // 3. Giải phóng số điện thoại (Giải quyết bài toán Unique Constraint)
        user.setPhoneNumber("DEL_" + user.getId() + "_" + System.currentTimeMillis());

        userRepository.save(user);
    }

    // GỬI MÃ OTP QUÊN MẬT KHẨU
    public void forgotPasswordRequest(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email không được để trống!");
        }
        String cleanEmail = email.trim();
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có email này!"));

        // Sinh mã OTP ngẫu nhiên gồm 6 chữ số
        String otp = String.format("%06d", new java.util.Random().nextInt(1000000));

        // Lưu vào Redis với TTL là 5 phút
        String redisKey = "otp:forgot-password:" + cleanEmail;
        redisTemplate.opsForValue().set(redisKey, otp, 5, java.util.concurrent.TimeUnit.MINUTES);

        // Gửi qua Email
        emailService.sendOtpEmail(cleanEmail, otp);
    }

    // XÁC NHẬN OTP VÀ ĐẶT LẠI MẬT KHẨU MỚI
    @Transactional
    public void forgotPasswordReset(com.uit.fooddelivery_api.modules.user.dtos.ResetPasswordRequestDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email không được để trống!");
        }
        if (dto.getOtp() == null || dto.getOtp().trim().isEmpty()) {
            throw new RuntimeException("Mã OTP không được để trống!");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().isEmpty()) {
            throw new RuntimeException("Mật khẩu mới không được để trống!");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        String cleanEmail = dto.getEmail().trim();
        String redisKey = "otp:forgot-password:" + cleanEmail;
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp == null || !storedOtp.equals(dto.getOtp().trim())) {
            throw new RuntimeException("Mã OTP không chính xác hoặc đã hết hạn!");
        }

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có email này!"));

        // Đặt mật khẩu mới
        user.setPassword(dto.getNewPassword());
        userRepository.save(user);

        // Xóa OTP khỏi Redis
        redisTemplate.delete(redisKey);
    }
}