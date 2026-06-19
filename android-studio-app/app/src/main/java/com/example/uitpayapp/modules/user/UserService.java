package com.example.uitpayapp.modules.user;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.user.models.requests.*;
import com.example.uitpayapp.modules.user.models.responses.*;

import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserService {

    // --- Các API xác thực & tài khoản ---
    @POST("api/v1/users/register")
    Call<ApiResponse<UserResponseDTO>> register(@Body RegisterRequestDTO request);

    @POST("api/v1/users/login")
    Call<ApiResponse<AuthResponseDTO>> login(@Body LoginRequestDTO request);

    @GET("api/v1/users/check-phone")
    Call<ApiResponse<CheckPhoneResponseDTO>> checkPhoneExists(@retrofit2.http.Query("phoneNumber") String phoneNumber);

    @GET("api/v1/users/me")
    Call<ApiResponse<UserResponseDTO>> getProfile();

    @Multipart
    @POST("api/v1/users/upload-avatar")
    Call<ApiResponse<String>> uploadAvatar(@Part MultipartBody.Part file);

    @PUT("api/v1/users/profile")
    Call<ApiResponse<UserResponseDTO>> updateProfile(@Body UpdateProfileDTO request);

    @PUT("api/v1/users/password")
    Call<ApiResponse<String>> changePassword(@Body ChangePasswordDTO request);

    @DELETE("api/v1/users/account")
    Call<ApiResponse<String>> deleteAccount();

    // --- Các API địa chỉ con (Nằm trong UserController) ---
    @POST("api/v1/users/addresses")
    Call<ApiResponse<AddressResponseDTO>> createAddress(@Body CreateAddressDTO request);

    @GET("api/v1/users/addresses")
    Call<ApiResponse<List<AddressResponseDTO>>> getAddresses();

    // --- Các API quên mật khẩu ---
    @POST("api/v1/users/forgot-password/request")
    Call<ApiResponse<String>> forgotPasswordRequest(@Body ForgotPasswordRequestDTO request);

    @POST("api/v1/users/forgot-password/reset")
    Call<ApiResponse<String>> forgotPasswordReset(@Body ResetPasswordRequestDTO request);
}