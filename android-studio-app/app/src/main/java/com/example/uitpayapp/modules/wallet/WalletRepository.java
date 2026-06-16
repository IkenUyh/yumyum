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
    public void getBalance(String token, final ApiCallback<BalanceResponse> callback) {
        walletService.getBalance("Bearer " + token).enqueue(new Callback<ApiResponse<BalanceResponse>>() {
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
    public void getTransactionHistory(String token, final ApiCallback<List<TransactionResponse>> callback) {
        walletService.getTransactions("Bearer " + token).enqueue(new Callback<ApiResponse<List<TransactionResponse>>>() {
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
}