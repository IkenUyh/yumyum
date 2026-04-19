package com.uit.zalopay_clone_api.modules.user.controllers;

import com.uit.zalopay_clone_api.modules.user.dtos.UserResponseDTO;
import com.uit.zalopay_clone_api.modules.user.entities.User;
import com.uit.zalopay_clone_api.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return UserResponseDTO.fromEntity(savedUser);
    }
}
