package com.example.uitpayapp.voucher;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherRepository {
    private final VoucherService voucherService;

    public VoucherRepository() {
        this.voucherService = RetrofitClient.getVoucherService();
    }

    public void getActiveVouchers(final ApiCallback<List<VoucherResponseDTO>> callback) {
        voucherService.getActiveVouchers().enqueue(new Callback<ApiResponse<List<VoucherResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<VoucherResponseDTO>>> call, Response<ApiResponse<List<VoucherResponseDTO>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<VoucherResponseDTO>> apiResponse = response.body();
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else if (apiResponse.getMessage() != null) {
                        callback.onError(apiResponse.getMessage());
                    } else {
                        callback.onError("Không có dữ liệu.");
                    }
                } else {
                    callback.onError("Lỗi kết nối: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<VoucherResponseDTO>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void exchangeVoucher(VoucherExchangeRequest request, final ApiCallback<VoucherResponseDTO> callback) {
        voucherService.exchangeCoinsForVoucher(request).enqueue(new Callback<ApiResponse<VoucherResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<VoucherResponseDTO>> call, Response<ApiResponse<VoucherResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<VoucherResponseDTO> apiResponse = response.body();
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else if (apiResponse.getMessage() != null) {
                        callback.onError(apiResponse.getMessage());
                    } else {
                        callback.onError("Đổi quà thành công.");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        if (errorBody.contains("message")) {
                            com.google.gson.JsonObject json = new com.google.gson.JsonParser().parse(errorBody).getAsJsonObject();
                            if (json.has("message")) {
                                callback.onError(json.get("message").getAsString());
                                return;
                            }
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                    callback.onError("Lỗi kết nối: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<VoucherResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
