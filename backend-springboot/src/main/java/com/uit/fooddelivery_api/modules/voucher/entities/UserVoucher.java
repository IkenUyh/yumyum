package com.uit.fooddelivery_api.modules.voucher.entities;

import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private Boolean isUsed = false;

    @Column(name = "acquired_at", nullable = false)
    @Builder.Default
    private LocalDateTime acquiredAt = LocalDateTime.now();
}
