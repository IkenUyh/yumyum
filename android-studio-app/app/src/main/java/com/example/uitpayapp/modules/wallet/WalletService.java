package com.example.uitpayapp.modules.wallet;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.wallet.models.requests.MerchantWalletTransferRequest;
import com.example.uitpayapp.modules.wallet.models.requests.MerchantWalletWithdrawRequest;
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
    Call<ApiResponse<BalanceResponse>> getBalance();

    // Lấy lịch sử biến động số dư
    @GET("api/v1/wallets/transactions")
    Call<ApiResponse<List<TransactionResponse>>> getTransactions();

    // Lấy số dư ví cửa hàng (merchant wallet)
    @GET("api/v1/merchant/wallets/balance")
    Call<ApiResponse<BalanceResponse>> getMerchantBalance();

    // Chuyển tiền từ ví cửa hàng sang ví cá nhân
    @POST("api/v1/merchant/wallets/transfer-to-personal")
    Call<ApiResponse<Void>> transferToPersonalWallet(@Body MerchantWalletTransferRequest request);

    // Rút tiền khỏi hệ thống
    @POST("api/v1/merchant/wallets/withdraw")
    Call<ApiResponse<Void>> withdrawMerchantBalance(@Body MerchantWalletWithdrawRequest request);


    // Dự phóng thêm API nạp tiền (vì backend đã cung cấp TopUpRequestDTO)
    @POST("api/v1/wallets/topup")
    Call<ApiResponse<Void>> topUp(@Body TopUpRequest request);

    // ZaloPay TopUp API
    @POST("api/v1/payments/zalopay/topup")
    Call<ApiResponse<java.util.Map<String, Object>>> createZaloPayTopUp(@retrofit2.http.Query("amount") long amount);

    // Query Order Status
    @GET("api/v1/payments/zalopay/order-status/{appTransId}")
    Call<ApiResponse<java.util.Map<String, Object>>> queryZaloPayOrderStatus(@retrofit2.http.Path("appTransId") String appTransId);

    // VNPay TopUp API
    @POST("api/v1/payments/vnpay/topup")
    Call<ApiResponse<java.util.Map<String, Object>>> createVNPayTopUp(@retrofit2.http.Query("amount") long amount);
}