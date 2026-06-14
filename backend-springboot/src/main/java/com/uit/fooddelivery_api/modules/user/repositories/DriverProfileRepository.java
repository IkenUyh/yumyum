package com.uit.fooddelivery_api.modules.user.repositories;

import com.uit.fooddelivery_api.modules.user.entities.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
}