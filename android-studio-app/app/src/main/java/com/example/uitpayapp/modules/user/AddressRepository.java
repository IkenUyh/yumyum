package com.example.uitpayapp.modules.user;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.user.models.requests.CreateAddressDTO;
import com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepository {
    private final AddressService addressService;

    public AddressRepository() {
        this.addressService = RetrofitClient.getAddressService();
    }

    // 1. Lấy danh sách địa chỉ cá nhân
    public void getMyAddresses(ApiCallback<List<AddressResponseDTO>> callback) {
        addressService.getMyAddresses().enqueue(new Callback<ApiResponse<List<AddressResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AddressResponseDTO>>> call, Response<ApiResponse<List<AddressResponseDTO>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AddressResponseDTO>>> call, Throwable t) {
                callback.onError("Mất kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    // 2. Thêm mới một địa chỉ
    public void createAddress(CreateAddressDTO dto, ApiCallback<AddressResponseDTO> callback) {
        addressService.createAddress(dto).enqueue(new Callback<ApiResponse<AddressResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<AddressResponseDTO>> call, Response<ApiResponse<AddressResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressResponseDTO>> call, Throwable t) {
                callback.onError("Mất kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    // 3. Đặt địa chỉ mặc định
    public void setDefaultAddress(Long addressId, ApiCallback<AddressResponseDTO> callback) {
        addressService.setDefaultAddress(addressId).enqueue(new Callback<ApiResponse<AddressResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<AddressResponseDTO>> call, Response<ApiResponse<AddressResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressResponseDTO>> call, Throwable t) {
                callback.onError("Mất kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    // 4. Xóa địa chỉ
    public void deleteAddress(Long addressId, ApiCallback<String> callback) {
        addressService.deleteAddress(addressId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError("Mất kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    // Hộp bóc tách gói dữ liệu chung
    private <T> void handleResponse(Response<ApiResponse<T>> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.getCode() == 200 || apiResponse.getCode() == 0) {
                callback.onSuccess(apiResponse.getData());
            } else {
                callback.onError(apiResponse.getMessage());
            }
        } else {
            callback.onError("Hệ thống phản hồi lỗi: " + response.code());
        }
    }
}