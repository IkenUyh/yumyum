package com.uit.fooddelivery_api.config.security;

import com.uit.fooddelivery_api.modules.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepository;

    // Day la cach Spring Security tim user theo so dien thoai
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> (UserDetails) userRepository.findByPhoneNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay user"));
    }

    // Cung cap thong tin de xac thuc
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Nhét userDetailsService vào thẳng constructor (bắt buộc đúng 1 tham số)
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Tam thoi de pass dang plain text de khop voi data mau cua anh, sau nay nen doi thanh BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}