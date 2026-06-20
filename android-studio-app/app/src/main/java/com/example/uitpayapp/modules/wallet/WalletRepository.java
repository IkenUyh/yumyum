package com.example.uitpayapp.modules.wallet;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse;
import com.example.uitpayapp.modules.wallet.models.responses.TransactionResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletRepository {
    private final WalletService walletService;

    public WalletRepository() {
        this.walletService = RetrofitClient.getWalletService();
    }

    // Hàm gọi lấy số dư
    public void getBalance(final ApiCallback<BalanceResponse> callback) {
        walletService.getBalance().enqueue(new Callback<ApiResponse<BalanceResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<BalanceResponse>> call, Response<ApiResponse<BalanceResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Không thể lấy thông tin số dư: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BalanceResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối hệ thống: " + t.getMessage());
            }
        });
    }

    // Hàm gọi lấy lịch sử giao dịch
    public void getTransactionHistory(final ApiCallback<List<TransactionResponse>> callback) {
        walletService.getTransactions().enqueue(new Callback<ApiResponse<List<TransactionResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TransactionResponse>>> call, Response<ApiResponse<List<TransactionResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Không thể tải lịch sử giao dịch.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TransactionResponse>>> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    // Hàm gọi nạp tiền
    public void topUp(com.example.uitpayapp.modules.wallet.models.requests.TopUpRequest request, final ApiCallback<String> callback) {
        walletService.topUp(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    callback.onError("Không thể nạp tiền: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError("Lỗi kết nối hệ thống: " + t.getMessage());
            }
        });
    }

    // Nạp tiền qua ZaloPay
    public void createZaloPayTopUp(long amount, final ApiCallback<java.util.Map<String, Object>> callback) {
        walletService.createZaloPayTopUp(amount).enqueue(new Callback<ApiResponse<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.Map<String, Object>>> call, Response<ApiResponse<java.util.Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Không thể tạo đơn ZaloPay: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.Map<String, Object>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Truy vấn trạng thái đơn ZaloPay
    public void queryZaloPayOrderStatus(String appTransId, final ApiCallback<java.util.Map<String, Object>> callback) {
        walletService.queryZaloPayOrderStatus(appTransId).enqueue(new Callback<ApiResponse<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.Map<String, Object>>> call, Response<ApiResponse<java.util.Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Không thể kiểm tra đơn hàng: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.Map<String, Object>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Nạp tiền qua VNPay
    public void createVNPayTopUp(long amount, final ApiCallback<java.util.Map<String, Object>> callback) {
        walletService.createVNPayTopUp(amount).enqueue(new Callback<ApiResponse<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.Map<String, Object>>> call, Response<ApiResponse<java.util.Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Không thể tạo đơn VNPay: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.Map<String, Object>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}