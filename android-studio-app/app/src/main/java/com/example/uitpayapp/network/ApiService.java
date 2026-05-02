package com.example.uitpayapp.network;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.models.LoginRequestDTO;
import com.example.uitpayapp.models.UserResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/v1/users/login")
    Call<ApiResponse<UserResponseDTO>> login(@Body LoginRequestDTO request);
}