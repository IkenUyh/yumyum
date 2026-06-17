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

    // Đăng ký
    public void register(String phoneNumber, String fullName, String email, String password, String referredByCode, ApiCallback<UserResponseDTO> callback) {
        RegisterRequestDTO dto = new RegisterRequestDTO(phoneNumber, fullName, email, password, referredByCode);
        userService.register(dto).enqueue(new Callback<ApiResponse<UserResponseDTO>>() {
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

    // 2. Lấy thông tin cá nhân
    public void getProfile(ApiCallback<UserResponseDTO> callback) {
        userService.getProfile().enqueue(new Callback<ApiResponse<UserResponseDTO>>() {
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

    // 2.1 Cập nhật thông tin cá nhân
    public void updateProfile(String fullName, String email, ApiCallback<UserResponseDTO> callback) {
        UpdateProfileDTO dto = new UpdateProfileDTO(fullName, email);
        userService.updateProfile(dto).enqueue(new Callback<ApiResponse<UserResponseDTO>>() {
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

    // 2.2 Tải ảnh đại diện lên
    public void uploadAvatar(java.io.File file, ApiCallback<String> callback) {
        MultipartBody.Part filePart = null;
        if (file != null && file.exists()) {
            okhttp3.RequestBody fileBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("avatarFile", file.getName(), fileBody);
        }
        userService.uploadAvatar(filePart).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // 3. Thêm địa chỉ mới (Mới gom vào)
    public void createAddress(String addressName, String recipientName, String phoneNumber,
                              String detailedAddress, BigDecimal latitude, BigDecimal longitude, Boolean isDefault,
                              ApiCallback<AddressResponseDTO> callback) {

        CreateAddressDTO dto = new CreateAddressDTO(addressName, recipientName, phoneNumber, detailedAddress, latitude, longitude, isDefault);
        userService.createAddress(dto).enqueue(new Callback<ApiResponse<AddressResponseDTO>>() {
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
    public void getAddresses(ApiCallback<List<AddressResponseDTO>> callback) {
        userService.getAddresses().enqueue(new Callback<ApiResponse<List<AddressResponseDTO>>>() {
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

    // 5. Yêu cầu mã OTP quên mật khẩu
    public void forgotPasswordRequest(String email, ApiCallback<String> callback) {
        ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO(email);
        userService.forgotPasswordRequest(dto).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // 6. Đặt lại mật khẩu mới bằng OTP
    public void forgotPasswordReset(String email, String otp, String newPassword, String confirmPassword, ApiCallback<String> callback) {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO(email, otp, newPassword, confirmPassword);
        userService.forgotPasswordReset(dto).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
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
            try {
                if (response.errorBody() != null) {
                    String errorJson = response.errorBody().string();
                    ApiResponse<?> errorResponse = new com.google.gson.Gson().fromJson(errorJson, ApiResponse.class);
                    if (errorResponse != null && errorResponse.getMessage() != null) {
                        callback.onError(errorResponse.getMessage());
                        return;
                    }
                }
            } catch (Exception e) {
                // Bỏ qua lỗi parse, trả về lỗi mặc định bên dưới
            }
            callback.onError("Lỗi hệ thống: " + response.code());
        }
    }
}