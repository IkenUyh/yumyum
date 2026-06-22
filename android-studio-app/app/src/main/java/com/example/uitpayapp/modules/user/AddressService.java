package com.example.uitpayapp.modules.user;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.user.models.requests.CreateAddressDTO;
import com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddressService {

    @GET("api/v1/addresses")
    Call<ApiResponse<List<AddressResponseDTO>>> getMyAddresses();

    @POST("api/v1/addresses")
    Call<ApiResponse<AddressResponseDTO>> createAddress(
            @Body CreateAddressDTO dto
    );

    @PUT("api/v1/addresses/{id}")
    Call<ApiResponse<AddressResponseDTO>> updateAddress(
            @Path("id") Long id,
            @Body CreateAddressDTO dto
    );

    @PUT("api/v1/addresses/{id}/set-default")
    Call<ApiResponse<AddressResponseDTO>> setDefaultAddress(
            @Path("id") Long addressId
    );

    @DELETE("api/v1/addresses/{id}")
    Call<ApiResponse<String>> deleteAddress(
            @Path("id") Long addressId
    );

    @GET("api/v1/addresses/default")
    Call<ApiResponse<AddressResponseDTO>> getDefaultAddress();
}