package com.uit.fooddelivery_api.modules.grouporder.repositories;

import com.uit.fooddelivery_api.modules.grouporder.entities.GroupOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupOrderItemRepository extends JpaRepository<GroupOrderItem, Long> {
}