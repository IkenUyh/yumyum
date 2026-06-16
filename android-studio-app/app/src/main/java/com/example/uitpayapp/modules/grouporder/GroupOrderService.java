package com.example.uitpayapp.modules.grouporder;

import com.example.uitpayapp.models.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GroupOrderService {

    @POST("api/v1/group-orders/create")
    Call<ApiResponse<String>> createRoom(
            @Query("restaurantId") Long restaurantId,
            @Header("Authorization") String token
    );

    @POST("api/v1/group-orders/{roomCode}/add-item")
    Call<ApiResponse<String>> addItem(
            @Path("roomCode") String roomCode,
            @Query("foodId") Long foodId,
            @Query("quantity") Integer quantity,
            @Query("optionsJson") String optionsJson,
            @Header("Authorization") String token
    );

    @POST("api/v1/group-orders/{roomCode}/checkout")
    Call<ApiResponse<Map<String, Object>>> checkoutAndSplitBill(
            @Path("roomCode") String roomCode,
            @Query("totalShippingFee") Double totalShippingFee,
            @Header("Authorization") String token
    );
}
