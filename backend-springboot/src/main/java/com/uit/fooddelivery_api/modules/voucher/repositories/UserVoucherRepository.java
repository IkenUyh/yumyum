package com.uit.fooddelivery_api.modules.voucher.repositories;

import com.uit.fooddelivery_api.modules.voucher.entities.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {

    List<UserVoucher> findByUserIdAndIsUsedFalse(Long userId);

    @Query("SELECT uv FROM UserVoucher uv WHERE uv.user.id = :userId AND uv.voucher.id = :voucherId AND uv.isUsed = false")
    Optional<UserVoucher> findUnusedByUserIdAndVoucherId(@Param("userId") Long userId, @Param("voucherId") Long voucherId);
    
    @Query("SELECT COUNT(uv) > 0 FROM UserVoucher uv WHERE uv.user.id = :userId AND uv.voucher.id = :voucherId")
    boolean existsByUserIdAndVoucherId(@Param("userId") Long userId, @Param("voucherId") Long voucherId);

    @Query(value = "SELECT * FROM user_vouchers WHERE user_id = :userId AND voucher_id = :voucherId AND is_used = true ORDER BY acquired_at DESC LIMIT 1", nativeQuery = true)
    Optional<UserVoucher> findFirstUsedByUserIdAndVoucherId(@Param("userId") Long userId, @Param("voucherId") Long voucherId);
}
