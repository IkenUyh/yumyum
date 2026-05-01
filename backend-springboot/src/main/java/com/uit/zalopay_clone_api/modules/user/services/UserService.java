package com.uit.zalopay_clone_api.modules.user.services;

import com.uit.zalopay_clone_api.modules.user.entities.User;
import com.uit.zalopay_clone_api.modules.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }
        
        return userRepository.save(user);
    }

    public User loginUser(String phoneNumber, String password) {
        User existingUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Số điện thoại không tồn tại!"));

        // 1. So sánh mật khẩu (hiện tại so sánh chuỗi thô, sau này dùng Bcrypt)
        if (!existingUser.getPassword().equals(password)) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        // 2. Đăng nhập thành công, trả về thông tin user
        return existingUser;
    }
}
