package com.example.uitpayapp.modules.user;

import com.example.uitpayapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface DriverService {

    // API báo cáo GPS ngầm mỗi 10 giây
    @PUT("api/v1/drivers/location")
    Call<ApiResponse<String>> updateLocation(
            @Header("Authorization") String token,
            @Query("lat") double lat,
            @Query("lng") double lng
    );

    // API tắt ứng dụng nghỉ chạy (Offline)
    @DELETE("api/v1/drivers/location")
    Call<ApiResponse<String>> goOffline(
            @Header("Authorization") String token
    );
}