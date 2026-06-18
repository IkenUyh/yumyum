package com.example.uitpayapp.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uitpayapp.home.network.BrandResponse;
import com.example.uitpayapp.home.network.DealResponse;
import com.example.uitpayapp.home.network.HomeApiService;
import com.example.uitpayapp.home.network.HomeCoreResponse;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.recommendeddeal.RecommendedDealModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final HomeApiService apiService;

    private final MutableLiveData<UiState<HomeCoreResponse>> coreData = new MutableLiveData<>();
    private final MutableLiveData<UiState<BrandResponse>> brandsData = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<RecommendedDealModel>>> dealsData = new MutableLiveData<>();

    private int currentDealsPage = 1;
    private int currentTabId = 0;
    private String currentAddressId = "";
    private boolean isDealsLoading = false;
    private boolean hasMoreDeals = true;
    private List<RecommendedDealModel> accumulatedDeals = new ArrayList<>();

    public HomeViewModel() {
        this.apiService = RetrofitClient.getHomeApiService();
    }

    public LiveData<UiState<HomeCoreResponse>> getCoreData() { return coreData; }
    public LiveData<UiState<BrandResponse>> getBrandsData() { return brandsData; }
    public LiveData<UiState<List<RecommendedDealModel>>> getDealsData() { return dealsData; }

    public void setAddressAndRefresh(String addressId) {
        this.currentAddressId = addressId;
        refreshAll();
    }

    public void refreshAll() {
        fetchCoreData();
        fetchBrandsData();
        resetAndFetchDeals(currentTabId);
    }

    private void fetchCoreData() {
        coreData.setValue(UiState.loading(null));
        apiService.getHomeCore(currentAddressId).enqueue(new Callback<ApiResponse<HomeCoreResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<HomeCoreResponse>> call, Response<ApiResponse<HomeCoreResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    android.util.Log.d("HomeViewModel", "getHomeCore: SUCCESS - Loaded from server.");
                    coreData.setValue(UiState.success(response.body().getData()));
                } else {
                    android.util.Log.w("HomeViewModel", "getHomeCore: FAIL (code " + response.code() + ") - Showing error.");
                    coreData.setValue(UiState.error("Không kết nối được server", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<HomeCoreResponse>> call, Throwable t) {
                android.util.Log.e("HomeViewModel", "getHomeCore: FAILURE (" + t.getMessage() + ") - Showing error.");
                coreData.setValue(UiState.error("Không kết nối được server", null));
            }
        });
    }

    private void fetchBrandsData() {
        brandsData.setValue(UiState.loading(null));
        apiService.getPopularBrands(currentAddressId).enqueue(new Callback<ApiResponse<BrandResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<BrandResponse>> call, Response<ApiResponse<BrandResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    BrandResponse data = response.body().getData();
                    if (data.getBrands() == null || data.getBrands().isEmpty()) {
                        brandsData.setValue(UiState.empty());
                    } else {
                        brandsData.setValue(UiState.success(data));
                    }
                } else {
                    android.util.Log.w("HomeViewModel", "getPopularBrands: FAIL (code " + response.code() + ") - Showing error.");
                    brandsData.setValue(UiState.error("Không kết nối được server", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BrandResponse>> call, Throwable t) {
                android.util.Log.e("HomeViewModel", "getPopularBrands: FAILURE (" + t.getMessage() + ") - Showing error.");
                brandsData.setValue(UiState.error("Không kết nối được server", null));
            }
        });
    }

    public void resetAndFetchDeals(int tabId) {
        currentTabId = tabId;
        currentDealsPage = 1;
        hasMoreDeals = true;
        accumulatedDeals.clear();
        dealsData.setValue(UiState.loading(accumulatedDeals));
        fetchDealsPage();
    }

    public void loadNextDealsPage() {
        if (!isDealsLoading && hasMoreDeals) {
            currentDealsPage++;
            fetchDealsPage();
        }
    }

    private void fetchDealsPage() {
        isDealsLoading = true;
        apiService.getRecommendedDeals(currentAddressId, currentTabId, currentDealsPage, 10)
                .enqueue(new Callback<DealResponse>() {
            @Override
            public void onResponse(Call<DealResponse> call, Response<DealResponse> response) {
                isDealsLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("HomeViewModel", "getRecommendedDeals (page " + currentDealsPage + "): SUCCESS - Loaded from server.");
                    List<RecommendedDealModel> newDeals = response.body().getDeals();
                    if (newDeals != null) {
                        accumulatedDeals.addAll(newDeals);
                    }
                    if (accumulatedDeals.isEmpty()) {
                        dealsData.setValue(UiState.empty());
                    } else {
                        dealsData.setValue(UiState.success(new ArrayList<>(accumulatedDeals)));
                    }
                    
                    if (currentDealsPage >= response.body().getTotalPages()) {
                        hasMoreDeals = false;
                    }
                } else {
                    android.util.Log.w("HomeViewModel", "getRecommendedDeals (page " + currentDealsPage + "): FAIL (code " + response.code() + ") - Showing error.");
                    if (accumulatedDeals.isEmpty()) {
                        dealsData.setValue(UiState.error("Không kết nối được server", null));
                    }
                }
            }

            @Override
            public void onFailure(Call<DealResponse> call, Throwable t) {
                isDealsLoading = false;
                android.util.Log.e("HomeViewModel", "getRecommendedDeals (page " + currentDealsPage + "): FAILURE (" + t.getMessage() + ") - Showing error.");
                if (accumulatedDeals.isEmpty()) {
                    dealsData.setValue(UiState.error("Không kết nối được server", null));
                }
            }
        });
    }

    private HomeCoreResponse getMockHomeCoreResponse() {
        String json = "{" +
                "  \"banners\": [" +
                "    { \"id\": \"b1\", \"imageUrl\": \"img_priority_banner1\", \"link\": \"\" }," +
                "    { \"id\": \"b2\", \"imageUrl\": \"img_priority_banner2\", \"link\": \"\" }," +
                "    { \"id\": \"b3\", \"imageUrl\": \"img_priority_banner3\", \"link\": \"\" }" +
                "  ]," +
                "  \"categories\": [" +
                "    { \"name\": \"Cơm\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_com + ", \"bgColor\": -1748736 }," +
                "    { \"name\": \"Bún Phở\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_bun_pho + ", \"bgColor\": -16743281 }," +
                "    { \"name\": \"Bánh mì\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_banh_mi + ", \"bgColor\": -4246004 }," +
                "    { \"name\": \"Fastfood\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_fastfood + ", \"bgColor\": -3790552 }," +
                "    { \"name\": \"Lẩu\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_lau + ", \"bgColor\": -2604267 }," +
                "    { \"name\": \"Đồ nướng\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_bbq + ", \"bgColor\": -4777216 }," +
                "    { \"name\": \"Cafe\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_ca_phe + ", \"bgColor\": -11651810 }," +
                "    { \"name\": \"Trà sữa\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_tra_sua + ", \"bgColor\": -7508125 }," +
                "    { \"name\": \"Ăn vặt\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_an_vat + ", \"bgColor\": -9823334 }," +
                "    { \"name\": \"Danh mục\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_all + ", \"bgColor\": -14142317, \"isSelectAll\": true }" +
                "  ]," +
                "  \"flashSales\": [" +
                "    { \"id\": \"d_1\", \"name\": \"Gà rán truyền thống\", \"price\": 45000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_chicken + ", \"description\": \"1 miếng gà rán giòn\" }," +
                "    { \"id\": \"d_2\", \"name\": \"Combo gà rán + khoai\", \"price\": 89000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_chicken + ", \"description\": \"2 miếng gà + khoai tây\" }," +
                "    { \"id\": \"d_3\", \"name\": \"Burger gà giòn\", \"price\": 39000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_chicken + ", \"description\": \"Burger gà với rau tươi\" }" +
                "  ]," +
                "  \"topics\": [" +
                "    {" +
                "      \"title\": \"Món Ngon Gần Bạn\"," +
                "      \"subtitle\": \"Khám phá ẩm thực xung quanh bạn\"," +
                "      \"items\": [" +
                "        { \"id\": \"f_1\", \"name\": \"Gà rán KFC\", \"price\": 45000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_chicken + ", \"description\": \"Gà rán giòn rụm\" }," +
                "        { \"id\": \"f_2\", \"name\": \"Trà sữa thái\", \"price\": 25000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_bubbletea + ", \"description\": \"Trà sữa thái xanh trân châu\" }," +
                "        { \"id\": \"f_3\", \"name\": \"Cà phê đen đá\", \"price\": 15000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_coffee + ", \"description\": \"Cà phê phin truyền thống\" }" +
                "      ]" +
                "    }," +
                "    {" +
                "      \"title\": \"Ưu Đãi Hôm Nay\"," +
                "      \"subtitle\": \"Khuyến mãi cực hot dành riêng cho bạn\"," +
                "      \"items\": [" +
                "        { \"id\": \"f_4\", \"name\": \"Pizza xúc xích\", \"price\": 89000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_pizza + ", \"description\": \"Pizza phô mai xúc xích\" }," +
                "        { \"id\": \"f_5\", \"name\": \"Gà cay phô mai\", \"price\": 55000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_chicken + ", \"description\": \"Gà xào bắp cải phô mai\" }," +
                "        { \"id\": \"f_6\", \"name\": \"Trà đào\", \"price\": 30000, \"imageResId\": " + com.example.uitpayapp.R.drawable.img_food_bubbletea + ", \"description\": \"Trà đào cam sả thanh mát\" }" +
                "      ]" +
                "    }" +
                "  ]" +
                "}";
        return new com.google.gson.Gson().fromJson(json, HomeCoreResponse.class);
    }

    private BrandResponse getMockBrandResponse() {
        java.util.List<com.example.uitpayapp.home.home_models.Restaurant> restaurantsList =
                HomeActivity.HomeRepository.getInstance().getRestaurants();
        String json = "{" +
                "  \"brands\": " + new com.google.gson.Gson().toJson(restaurantsList) +
                "}";
        return new com.google.gson.Gson().fromJson(json, BrandResponse.class);
    }
}
