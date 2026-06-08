package com.uit.fooddelivery_api.modules.merchant.services;

import com.uit.fooddelivery_api.modules.merchant.dtos.SubmitRequestDTO;
import com.uit.fooddelivery_api.modules.merchant.entities.MerchantRequest;
import com.uit.fooddelivery_api.modules.merchant.repositories.MerchantRequestRepository;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.entities.Role;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.repositories.UserRepository;
import com.uit.fooddelivery_api.modules.user.services.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantRequestService {

    private final MerchantRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CloudinaryService cloudinaryService; // Tái sử dụng service upload ảnh

    @Transactional
    public MerchantRequest submitRequest(SubmitRequestDTO dto, MultipartFile licenseFile, User user) throws Exception {
        // 1. Chặn spam: Không cho gửi thêm nếu đang có đơn PENDING
        List<MerchantRequest> existing = requestRepository.findByUserId(user.getId());
        boolean hasPending = existing.stream().anyMatch(r -> r.getStatus().equals("PENDING"));
        if (hasPending) {
            throw new RuntimeException("Bạn đang có một yêu cầu chờ duyệt, vui lòng không gửi lại!");
        }

        // 2. Upload ảnh Giấy phép kinh doanh lên Cloudinary
        String licenseUrl = null;
        if (licenseFile != null && !licenseFile.isEmpty()) {
            licenseUrl = cloudinaryService.uploadAvatar(licenseFile);
        }

        // 3. Tạo yêu cầu
        MerchantRequest req = MerchantRequest.builder()
                .user(user)
                .storeName(dto.getStoreName())
                .storeAddress(dto.getStoreAddress())
                .storePhone(dto.getStorePhone())
                .confirmationCode(dto.getConfirmationCode())
                .businessLicenseUrl(licenseUrl)
                .status("PENDING")
                .build();

        return requestRepository.save(req);
    }

    public List<MerchantRequest> getPendingRequests() {
        return requestRepository.findByStatus("PENDING");
    }

    @Transactional
    public MerchantRequest approveRequest(Long requestId) {
        MerchantRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu!"));

        if (!req.getStatus().equals("PENDING")) {
            throw new RuntimeException("Yêu cầu này đã được xử lý rồi!");
        }

        req.setStatus("APPROVED");
        requestRepository.save(req);

        // AUTO 1: Nâng cấp quyền User lên MERCHANT
        User user = req.getUser();
        user.setRole(Role.MERCHANT);
        userRepository.save(user);

        // AUTO 2: Tạo sẵn luôn Cửa hàng cho họ
        Restaurant restaurant = Restaurant.builder()
                .merchant(user)
                .name(req.getStoreName())
                .address(req.getStoreAddress())
                .openTime(LocalTime.of(8, 0)) // Set mặc định 8h sáng
                .closeTime(LocalTime.of(22, 0)) // Đóng lúc 10h tối
                .isActive(true)
                .build();
        restaurantRepository.save(restaurant);

        return req;
    }

    @Transactional
    public MerchantRequest rejectRequest(Long requestId) {
        MerchantRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu!"));

        req.setStatus("REJECTED");
        return requestRepository.save(req);
    }
}