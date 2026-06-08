package com.uit.fooddelivery_api.modules.merchant.entities;

import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchant_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_address", nullable = false)
    private String storeAddress;

    @Column(name = "store_phone", nullable = false, length = 20)
    private String storePhone;

    @Column(name = "business_license_url")
    private String businessLicenseUrl;

    @Column(nullable = false, length = 30)
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "confirmation_code", length = 20)
    private String confirmationCode;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}