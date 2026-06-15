package com.uit.fooddelivery_api.modules.system.repositories;

import com.uit.fooddelivery_api.modules.system.entities.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long> {
    Optional<SystemParameter> findByParamKey(String paramKey);
}