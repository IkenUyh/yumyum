package com.uit.fooddelivery_api.modules.user.services;

import com.uit.fooddelivery_api.modules.user.dtos.CreateAddressDTO;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.entities.UserAddress;
import com.uit.fooddelivery_api.modules.user.repositories.UserAddressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository addressRepository;

    // 1. Lấy danh sách địa chỉ của User hiện tại
    public List<UserAddress> getAddressesByUser(User user) {
        return addressRepository.findByUserId(user.getId());
    }

    // 2. Thêm địa chỉ mới
    @Transactional
    public UserAddress createAddress(CreateAddressDTO dto, User user) {
        boolean shouldBeDefault = dto.getIsDefault() != null && dto.getIsDefault();

        // Nếu đây là địa chỉ đầu tiên của user, tự động set làm mặc định
        List<UserAddress> existingAddresses = addressRepository.findByUserId(user.getId());
        if (existingAddresses.isEmpty()) {
            shouldBeDefault = true;
        } else if (shouldBeDefault) {
            // Nếu đánh dấu mặc định, bỏ mặc định các địa chỉ cũ trước
            resetDefaultAddress(user.getId());
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .addressName(dto.getAddressName())
                .recipientName(dto.getRecipientName())
                .phoneNumber(dto.getPhoneNumber())
                .detailedAddress(dto.getDetailedAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .isDefault(shouldBeDefault)
                .build();

        return addressRepository.save(address);
    }

    // 3. Đặt một địa chỉ có sẵn thành mặc định
    @Transactional
    public UserAddress setDefaultAddress(Long addressId, User user) {
        UserAddress targetAddress = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ này hoặc bạn không có quyền sở hữu!"));

        // Bỏ mặc định tất cả các địa chỉ cũ của user
        resetDefaultAddress(user.getId());

        // Đặt địa chỉ mục tiêu làm mặc định
        targetAddress.setIsDefault(true);
        return addressRepository.save(targetAddress);
    }

    // 4. Xóa địa chỉ
    @Transactional
    public void deleteAddress(Long addressId, User user) {
        UserAddress address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ cần xóa hoặc không có quyền sở hữu!"));

        // Nếu xóa đúng địa chỉ đang mặc định thì cần chuyển quyền mặc định sang địa chỉ khác (nếu còn)
        if (address.getIsDefault()) {
            addressRepository.delete(address);
            List<UserAddress> remaining = addressRepository.findByUserId(user.getId());
            if (!remaining.isEmpty()) {
                UserAddress nextDefault = remaining.get(0);
                nextDefault.setIsDefault(true);
                addressRepository.save(nextDefault);
            }
        } else {
            addressRepository.delete(address);
        }
    }

    // Hàm tiện ích loại bỏ toàn bộ trạng thái mặc định cũ
    private void resetDefaultAddress(Long userId) {
        List<UserAddress> defaultAddresses = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        for (UserAddress addr : defaultAddresses) {
            addr.setIsDefault(false);
        }
        addressRepository.saveAll(defaultAddresses);
    }
}