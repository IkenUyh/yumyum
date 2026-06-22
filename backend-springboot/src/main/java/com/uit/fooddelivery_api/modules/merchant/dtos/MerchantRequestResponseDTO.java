package com.uit.fooddelivery_api.modules.merchant.dtos;

import com.uit.fooddelivery_api.modules.merchant.entities.MerchantRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MerchantRequestResponseDTO {
    private Long id;
    private Long userId;
    private String storeName;
    private String storeAddress;
    private String storePhone;
    private String businessLicenseUrl;
    private String status;
    private String confirmationCode;
    private String ownerName;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private LocalDateTime createdAt;

    public static MerchantRequestResponseDTO fromEntity(MerchantRequest req) {
        return MerchantRequestResponseDTO.builder()
                .id(req.getId())
                .userId(req.getUser().getId())
                .storeName(req.getStoreName())
                .storeAddress(req.getStoreAddress())
                .storePhone(req.getStorePhone())
                .businessLicenseUrl(req.getBusinessLicenseUrl())
                .status(req.getStatus())
                .confirmationCode(req.getConfirmationCode())
                .ownerName(req.getUser().getFullName())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .createdAt(req.getCreatedAt())
                .build();
    }
}