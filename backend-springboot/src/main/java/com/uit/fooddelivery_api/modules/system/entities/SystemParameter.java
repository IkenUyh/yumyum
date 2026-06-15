package com.uit.fooddelivery_api.modules.system.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_parameters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "param_key", unique = true, nullable = false)
    private String paramKey;

    @Column(name = "param_value", nullable = false)
    private String paramValue;

    private String description;
}