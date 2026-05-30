package com.uit.fooddelivery_api.modules.wallet.entities;

import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ 1-1: Một User có một Ví
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Dùng BigDecimal để lưu tiền cho chuẩn xác, không bị sai số như Double
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
}