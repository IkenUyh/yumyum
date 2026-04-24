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
}
