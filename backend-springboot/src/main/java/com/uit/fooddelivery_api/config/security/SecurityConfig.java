package com.uit.fooddelivery_api.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Chi cong khai API login va register va forgot password
                        .requestMatchers("/api/v1/users/login", "/api/v1/users/register", "/api/v1/users/forgot-password/**", "/api/v1/users/check-phone").permitAll()
                        // Cho phep xem danh sach nha hang, mon an va tim kiem thoai mai khong can Token
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/restaurants/**", "/api/v1/foods/**", "/api/v1/search/**", "/api/v1/payments/zalopay/callback", "/api/v1/payments/vnpay/ipn", "/api/v1/payments/vnpay/return", "/api/v1/categories/**", "/api/v1/home/**").permitAll()
                        // Tat ca API con lai deu phai xac thuc qua Token
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}