package com.uit.fooddelivery_api.modules.notification.repositories;

import com.uit.fooddelivery_api.modules.notification.entities.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    List<UserFcmToken> findByUserId(Long userId);

    Optional<UserFcmToken> findByFcmToken(String fcmToken);

    void deleteByFcmToken(String fcmToken);

    void deleteByUserIdAndFcmToken(Long userId, String fcmToken);
}
