package com.example.uitpayapp.modules.system;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.system.models.responses.SystemParameterResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SystemParameterRepository {
    private final SystemParameterService systemParameterService;

    public SystemParameterRepository() {
        this.systemParameterService = RetrofitClient.getSystemParameterService();
    }

    // Lấy toàn bộ danh sách cấu hình
    public void getAllParameters(final ApiCallback<List<SystemParameterResponse>> callback) {
        systemParameterService.getAllParameters().enqueue(new Callback<ApiResponse<List<SystemParameterResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SystemParameterResponse>>> call, Response<ApiResponse<List<SystemParameterResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Không thể lấy danh sách cấu hình: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SystemParameterResponse>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Cập nhật một tham số hệ thống cấu hình bất kỳ
    public void updateParameter(String paramKey, String newValue, final ApiCallback<SystemParameterResponse> callback) {
        systemParameterService.updateParameter(paramKey, newValue).enqueue(new Callback<ApiResponse<SystemParameterResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SystemParameterResponse>> call, Response<ApiResponse<SystemParameterResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else if (response.code() == 403) {
                    callback.onError("Bạn không có quyền thay đổi cấu hình này (Yêu cầu quyền Admin).");
                } else {
                    callback.onError("Cập nhật thất bại hoặc không tìm thấy tham số.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SystemParameterResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }
}