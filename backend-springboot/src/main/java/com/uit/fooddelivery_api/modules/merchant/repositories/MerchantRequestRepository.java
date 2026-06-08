package com.uit.fooddelivery_api.modules.merchant.repositories;

import com.uit.fooddelivery_api.modules.merchant.entities.MerchantRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRequestRepository extends JpaRepository<MerchantRequest, Long> {
    List<MerchantRequest> findByStatus(String status);
    List<MerchantRequest> findByUserId(Long userId);
}