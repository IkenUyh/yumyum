package com.example.uitpayapp.modules.merchant;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.merchant.models.requests.SubmitRequestDTO;
import com.example.uitpayapp.modules.merchant.models.responses.MerchantRequestResponseDTO;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MerchantRepository {
    private final MerchantService merchantService;

    public MerchantRepository() {
        this.merchantService = RetrofitClient.getMerchantService();
    }

    // 1. Gửi đơn đăng ký (Kèm File giấy phép)
    public void submitRequest(SubmitRequestDTO dto, File file, ApiCallback<MerchantRequestResponseDTO> callback) {
        // Chuyển đổi các trường Text sang RequestBody nhằm tương thích với Form-Data Multipart
        RequestBody storeName = RequestBody.create(MediaType.parse("text/plain"), dto.getStoreName());
        RequestBody storeAddress = RequestBody.create(MediaType.parse("text/plain"), dto.getStoreAddress());
        RequestBody storePhone = RequestBody.create(MediaType.parse("text/plain"), dto.getStorePhone());
        RequestBody confirmationCode = RequestBody.create(MediaType.parse("text/plain"), dto.getConfirmationCode());

        MultipartBody.Part filePart = null;
        if (file != null && file.exists()) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            // Key "licenseFile" trùng khớp chính xác với @RequestParam backend yêu cầu
            filePart = MultipartBody.Part.createFormData("licenseFile", file.getName(), fileBody);
        }

        merchantService.submitRequest(storeName, storeAddress, storePhone, confirmationCode, filePart)
                .enqueue(new Callback<ApiResponse<MerchantRequestResponseDTO>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MerchantRequestResponseDTO>> call, Response<ApiResponse<MerchantRequestResponseDTO>> response) {
                        handleResponse(response, callback);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MerchantRequestResponseDTO>> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    // 2. Lấy danh sách đơn đang chờ duyệt (Admin)
    public void getPendingRequests(ApiCallback<List<MerchantRequestResponseDTO>> callback) {
        merchantService.getPendingRequests().enqueue(new Callback<ApiResponse<List<MerchantRequestResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MerchantRequestResponseDTO>>> call, Response<ApiResponse<List<MerchantRequestResponseDTO>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MerchantRequestResponseDTO>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // 3. Duyệt đơn đăng ký
    public void approveRequest(Long id, ApiCallback<MerchantRequestResponseDTO> callback) {
        merchantService.approveRequest(id).enqueue(new Callback<ApiResponse<MerchantRequestResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<MerchantRequestResponseDTO>> call, Response<ApiResponse<MerchantRequestResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<MerchantRequestResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // 4. Từ chối đơn đăng ký
    public void rejectRequest(Long id, ApiCallback<MerchantRequestResponseDTO> callback) {
        merchantService.rejectRequest(id).enqueue(new Callback<ApiResponse<MerchantRequestResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<MerchantRequestResponseDTO>> call, Response<ApiResponse<MerchantRequestResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<MerchantRequestResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Hàm Helper bóc tách ApiResponse qua ApiCallback tập trung
    private <T> void handleResponse(Response<ApiResponse<T>> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            // Tùy theo cấu trúc ApiResponse của bạn, giả định code thành công thường là 200 hoặc biến boolean status
            if (apiResponse.getData() != null) {
                callback.onSuccess(apiResponse.getData());
            } else {
                callback.onError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Dữ liệu trống");
            }
        } else {
            callback.onError("Lỗi hệ thống: " + response.code());
        }
    }
}