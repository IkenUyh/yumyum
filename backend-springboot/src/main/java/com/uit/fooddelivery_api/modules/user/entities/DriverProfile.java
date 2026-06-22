package com.uit.fooddelivery_api.modules.user.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {

    @Id
    @Column(name = "user_id")
    private Long userId; // Dùng chung ID với bảng users

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Báo cho Hibernate biết user_id vừa là PK vừa là FK
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "vehicle_plate", nullable = false, length = 20)
    private String vehiclePlate;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Column(nullable = false, length = 20)
    private String status; // ONLINE, OFFLINE, BUSY

    @Column(name = "current_order_id")
    private Long currentOrderId;

    // THUỘC TÍNH NÀY LÀ CÁI ĐANG BỊ THIẾU ĐỂ GHÉP ĐƠN
    @Column(name = "current_order_count")
    private Integer currentOrderCount;
}