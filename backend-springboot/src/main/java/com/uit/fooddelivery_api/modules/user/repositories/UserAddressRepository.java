package com.uit.fooddelivery_api.modules.user.repositories;

import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.entities.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    // Lấy tất cả địa chỉ của một user cụ thể
    List<UserAddress> findByUserId(Long userId);

    // Tìm địa chỉ đảm bảo đúng quyền sở hữu của user
    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);

    // Tìm các địa chỉ đang được đặt làm mặc định của user để xử lý logic reset cờ
    List<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);
}