package com.uit.fooddelivery_api.modules.user.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.user.dtos.AddressResponseDTO;
import com.uit.fooddelivery_api.modules.user.dtos.CreateAddressDTO;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.entities.UserAddress;
import com.uit.fooddelivery_api.modules.user.services.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService addressService;

    // API: Lấy toàn bộ danh sách địa chỉ cá nhân
    @GetMapping
    public ApiResponse<List<AddressResponseDTO>> getMyAddresses(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<AddressResponseDTO> list = addressService.getAddressesByUser(currentUser)
                .stream()
                .map(AddressResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    // API: Lấy địa chỉ mặc định của cá nhân
    @GetMapping("/default")
    public ApiResponse<AddressResponseDTO> getDefaultAddress(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        UserAddress defaultAddress = addressService.getDefaultAddress(currentUser);
        if (defaultAddress == null) {
            return ApiResponse.success(null);
        }
        return ApiResponse.success(AddressResponseDTO.fromEntity(defaultAddress));
    }

    // API: Tạo mới một địa chỉ vào sổ tay
    @PostMapping
    public ApiResponse<AddressResponseDTO> createAddress(
            Authentication authentication,
            @RequestBody CreateAddressDTO dto) {

        User currentUser = (User) authentication.getPrincipal();
        UserAddress savedAddress = addressService.createAddress(dto, currentUser);
        return ApiResponse.success(AddressResponseDTO.fromEntity(savedAddress));
    }

    // API: Đổi một địa chỉ bất kỳ thành địa chỉ mặc định giao hàng
    @PutMapping("/{id}/set-default")
    public ApiResponse<AddressResponseDTO> setDefaultAddress(
            @PathVariable("id") Long addressId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        UserAddress updatedAddress = addressService.setDefaultAddress(addressId, currentUser);
        return ApiResponse.success(AddressResponseDTO.fromEntity(updatedAddress));
    }

    // API: Xóa địa chỉ khỏi danh sách
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAddress(
            @PathVariable("id") Long addressId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        addressService.deleteAddress(addressId, currentUser);
        return ApiResponse.success("Đã xóa địa chỉ thành công khỏi sổ tay!");
    }
}