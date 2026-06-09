package com.uit.fooddelivery_api.modules.food.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "food_option_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private FoodOptionGroup group;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "additional_price", precision = 19, scale = 2)
    private BigDecimal additionalPrice;

    @Column(name = "is_available")
    private Boolean isAvailable;
}