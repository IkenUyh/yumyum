package com.example.uitpayapp.modules.merchant;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.merchant.models.responses.MerchantRequestResponseDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MerchantService {

    @Multipart
    @POST("api/v1/merchant-requests/submit")
    Call<ApiResponse<MerchantRequestResponseDTO>> submitRequest(
            @Part("storeName") RequestBody storeName,
            @Part("storeAddress") RequestBody storeAddress,
            @Part("storePhone") RequestBody storePhone,
            @Part("confirmationCode") RequestBody confirmationCode,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part MultipartBody.Part licenseFile
    );

    @GET("api/v1/merchant-requests")
    Call<ApiResponse<List<MerchantRequestResponseDTO>>> getRequestsByStatus(@retrofit2.http.Query("status") String status);

    @PUT("api/v1/merchant-requests/{id}/approve")
    Call<ApiResponse<MerchantRequestResponseDTO>> approveRequest(@Path("id") Long id);

    @PUT("api/v1/merchant-requests/{id}/reject")
    Call<ApiResponse<MerchantRequestResponseDTO>> rejectRequest(@Path("id") Long id);
}