package com.uit.fooddelivery_api.modules.loyalty.entities;

import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "loyalty_points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "current_points")
    private Integer currentPoints;

    @Column(name = "checkin_streak")
    private Integer checkinStreak;

    @Column(name = "last_checkin_date")
    private LocalDate lastCheckinDate;
}