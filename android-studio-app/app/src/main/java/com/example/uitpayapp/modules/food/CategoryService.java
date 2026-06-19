package com.example.uitpayapp.modules.food;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.food.models.requests.CreateCategoryRequest;
import com.example.uitpayapp.modules.food.models.responses.CategoryResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;


public interface CategoryService {

    //Lấy tất cả danh mục (dùng để load dropdown chọn danh mục)
    @GET("api/v1/categories")
    Call<ApiResponse<List<CategoryResponse>>> getAllCategories();

    // Lấy tất cả danh mục theo nhà hàng (chỉ trả về category có món của quán đó)
    @GET("api/v1/categories/by-restaurant/{restaurantId}")
    Call<ApiResponse<List<CategoryResponse>>> getCategoriesByRestaurant(
            @Path("restaurantId") Long restaurantId
    );

    // Lấy danh sách món ăn theo danh mục
    @GET("api/v1/categories/{id}/foods")
    Call<ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.FoodResponse>>> getFoodsByCategory(
            @Path("id") Long categoryId
    );

    // Tạo danh mục mới (chủ quán)
    @POST("api/v1/categories")
    Call<ApiResponse<CategoryResponse>> createCategory(
            @Body CreateCategoryRequest request
    );

    // Cập nhật tên danh mục (chủ quán)
    @PUT("api/v1/categories/{id}")
    Call<ApiResponse<CategoryResponse>> updateCategory(
            @Path("id") Long categoryId,
            @Body CreateCategoryRequest request
    );
}
