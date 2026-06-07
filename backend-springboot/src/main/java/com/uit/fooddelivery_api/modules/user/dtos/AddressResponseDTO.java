package com.uit.fooddelivery_api.modules.user.dtos;

import com.uit.fooddelivery_api.modules.user.entities.UserAddress;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AddressResponseDTO {
    private Long id;
    private String addressName;
    private String recipientName;
    private String phoneNumber;
    private String detailedAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isDefault;

    public static AddressResponseDTO fromEntity(UserAddress address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .addressName(address.getAddressName())
                .recipientName(address.getRecipientName())
                .phoneNumber(address.getPhoneNumber())
                .detailedAddress(address.getDetailedAddress())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .isDefault(address.getIsDefault())
                .build();
    }
}