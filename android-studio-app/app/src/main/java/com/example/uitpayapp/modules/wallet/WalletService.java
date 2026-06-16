package com.example.uitpayapp.modules.wallet;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.wallet.models.requests.TopUpRequest;
import com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse;
import com.example.uitpayapp.modules.wallet.models.responses.TransactionResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface WalletService {

    // Lấy số dư ví hiện tại
    @GET("api/v1/wallets/balance")
    Call<ApiResponse<BalanceResponse>> getBalance(@Header("Authorization") String token);

    // Lấy lịch sử biến động số dư
    @GET("api/v1/wallets/transactions")
    Call<ApiResponse<List<TransactionResponse>>> getTransactions(@Header("Authorization") String token);

    // Dự phóng thêm API nạp tiền (vì backend đã cung cấp TopUpRequestDTO)
    @POST("api/v1/wallets/topup")
    Call<ApiResponse<Void>> topUp(@Header("Authorization") String token, @Body TopUpRequest request);
}