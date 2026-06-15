package com.example.uitpayapp.modules.user;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.user.models.requests.*;
import com.example.uitpayapp.modules.user.models.responses.*;

import java.math.BigDecimal;
import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final UserService userService;

    public UserRepository() {
        this.userService = RetrofitClient.getUserService();
    }

    private String formatToken(String token) {
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    // 1. Đăng nhập
    public void login(String phoneNumber, String password, ApiCallback<AuthResponseDTO> callback) {
        LoginRequestDTO dto = new LoginRequestDTO(phoneNumber, password);
        userService.login(dto).enqueue(new Callback<ApiResponse<AuthResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponseDTO>> call, Response<ApiResponse<AuthResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponseDTO>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // 2. Lấy thông tin cá nhân
    public void getProfile(String token, ApiCallback<UserResponseDTO> callback) {
        userService.getProfile(formatToken(token)).enqueue(new Callback<ApiResponse<UserResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponseDTO>> call, Response<ApiResponse<UserResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponseDTO>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // 3. Thêm địa chỉ mới (Mới gom vào)
    public void createAddress(String token, String addressName, String recipientName, String phoneNumber,
                              String detailedAddress, BigDecimal latitude, BigDecimal longitude, Boolean isDefault,
                              ApiCallback<AddressResponseDTO> callback) {

        CreateAddressDTO dto = new CreateAddressDTO(addressName, recipientName, phoneNumber, detailedAddress, latitude, longitude, isDefault);
        userService.createAddress(formatToken(token), dto).enqueue(new Callback<ApiResponse<AddressResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<AddressResponseDTO>> call, Response<ApiResponse<AddressResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressResponseDTO>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // 4. Lấy danh sách địa chỉ (Mới gom vào)
    public void getAddresses(String token, ApiCallback<List<AddressResponseDTO>> callback) {
        userService.getAddresses(formatToken(token)).enqueue(new Callback<ApiResponse<List<AddressResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AddressResponseDTO>>> call, Response<ApiResponse<List<AddressResponseDTO>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AddressResponseDTO>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Hàm Helper bóc tách gói dữ liệu dùng chung nội bộ lớp
    private <T> void handleResponse(Response<ApiResponse<T>> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.getCode() == 200 || apiResponse.getCode() == 0) {
                callback.onSuccess(apiResponse.getData());
            } else {
                callback.onError(apiResponse.getMessage());
            }
        } else {
            callback.onError("Lỗi hệ thống: " + response.code());
        }
    }
}