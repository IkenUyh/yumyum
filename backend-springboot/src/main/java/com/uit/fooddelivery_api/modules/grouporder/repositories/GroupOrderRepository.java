package com.uit.fooddelivery_api.modules.grouporder.repositories;

import com.uit.fooddelivery_api.modules.grouporder.entities.GroupOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GroupOrderRepository extends JpaRepository<GroupOrder, Long> {
    Optional<GroupOrder> findByRoomCode(String roomCode);
}