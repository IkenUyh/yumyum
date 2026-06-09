package com.uit.fooddelivery_api.modules.food.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "food_option_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "max_choices")
    private Integer maxChoices;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<FoodOptionItem> optionItems;
}