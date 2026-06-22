package com.example.uitpayapp.modules.cart;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.cart.models.requests.CartItemRequestDTO;
import com.example.uitpayapp.modules.cart.models.responses.CartItemResponseDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartService {

    @GET("api/v1/carts")
    Call<ApiResponse<List<CartItemResponseDTO>>> getCart();

    @POST("api/v1/carts")
    Call<ApiResponse<String>> addOrUpdateItem(
            @Body CartItemRequestDTO dto
    );

    @DELETE("api/v1/carts/{itemId}")
    Call<ApiResponse<String>> removeItem(
            @Path("itemId") Long itemId
    );

    @DELETE("api/v1/carts/clear")
    Call<ApiResponse<String>> clearCart();

    @GET("api/v1/carts/count")
    Call<ApiResponse<Integer>> getCartCount();
}