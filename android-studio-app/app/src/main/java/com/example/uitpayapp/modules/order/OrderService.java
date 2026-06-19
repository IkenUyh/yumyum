package com.example.uitpayapp.modules.order;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.order.models.requests.*;
import com.example.uitpayapp.modules.order.models.responses.OrderResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface OrderService {

    @POST("api/v1/orders/preview")
    Call<ApiResponse<com.example.uitpayapp.modules.order.models.responses.OrderPreviewResponse>> previewOrder(@Body CreateOrderRequest request);

    @POST("api/v1/orders")
    Call<ApiResponse<OrderResponse>> createOrder(@Body CreateOrderRequest request);

    @PUT("api/v1/orders/{orderId}/cancel")
    Call<ApiResponse<OrderResponse>> cancelOrder(@Path("orderId") Long orderId, @Body CancelOrderRequest request);

    @GET("api/v1/orders/available")
    Call<ApiResponse<List<OrderResponse>>> getAvailableOrders();

    @PUT("api/v1/orders/{id}/accept")
    Call<ApiResponse<OrderResponse>> acceptOrder(@Path("id") Long orderId);

    @PUT("api/v1/orders/{id}/complete")
    Call<ApiResponse<OrderResponse>> completeOrder(@Path("id") Long orderId);

    @GET("api/v1/orders/history/customer")
    Call<ApiResponse<List<OrderResponse>>> getCustomerHistory();

    @GET("api/v1/orders/history/merchant")
    Call<ApiResponse<List<OrderResponse>>> getMerchantHistory();

    @PUT("api/v1/orders/{orderId}/merchant-ready")
    Call<ApiResponse<OrderResponse>> merchantReady(@Path("orderId") Long orderId);

    @PUT("api/v1/orders/{orderId}/driver-pickup")
    Call<ApiResponse<OrderResponse>> driverPickup(@Path("orderId") Long orderId, @Body ConfirmPickupRequest request);

    @PUT("api/v1/orders/{orderId}/driver-complete")
    Call<ApiResponse<OrderResponse>> driverComplete(@Path("orderId") Long orderId, @Body ConfirmDeliveryRequest request);

    @PUT("api/v1/orders/{orderId}/remove-item")
    Call<ApiResponse<OrderResponse>> removeItemFromOrder(@Path("orderId") Long orderId, @Body RemoveItemRequest request);

    @PUT("api/v1/orders/{orderId}/merchant-complete")
    Call<ApiResponse<OrderResponse>> merchantCompleteOrder(@Path("orderId") Long orderId);
}
