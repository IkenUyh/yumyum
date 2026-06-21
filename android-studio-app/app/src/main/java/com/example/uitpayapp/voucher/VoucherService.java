package com.example.uitpayapp.voucher;

import com.example.uitpayapp.models.ApiResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface VoucherService {
    @GET("api/v1/vouchers")
    Call<ApiResponse<List<VoucherResponseDTO>>> getActiveVouchers();

    @retrofit2.http.POST("api/v1/vouchers/exchange")
    Call<ApiResponse<VoucherResponseDTO>> exchangeCoinsForVoucher(@retrofit2.http.Body VoucherExchangeRequest request);
}
