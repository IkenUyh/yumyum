package com.uit.fooddelivery_api.modules.restaurant.entities;

import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private User merchant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(precision = 10, scale = 8)
    private java.math.BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private java.math.BigDecimal longitude;

    @Column(name = "is_accepting_orders")
    private Boolean isAcceptingOrders;

    @Column(name = "max_pending_orders")
    private Integer maxPendingOrders;

    @Column(name = "rating_average", precision = 3, scale = 1)
    private java.math.BigDecimal ratingAverage;

    @Column(name = "review_count")
    private Integer reviewCount;
}