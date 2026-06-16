package com.example.uitpayapp.modules.system;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.system.models.responses.SystemParameterResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SystemParameterService {

    // Lấy toàn bộ danh sách cấu hình hệ thống
    @GET("api/v1/system-parameters")
    Call<ApiResponse<List<SystemParameterResponse>>> getAllParameters();

    // Cập nhật cấu hình hệ thống (Yêu cầu quyền ADMIN nên cấu hình thêm Header Authorization)
    @PUT("api/v1/system-parameters/{paramKey}")
    Call<ApiResponse<SystemParameterResponse>> updateParameter(
            @Header("Authorization") String token,
            @Path("paramKey") String paramKey,
            @Query("newValue") String newValue
    );
}