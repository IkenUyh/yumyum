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
import java.util.List;

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
        apiService.getHomeCore(currentAddressId).enqueue(new Callback<HomeCoreResponse>() {
            @Override
            public void onResponse(Call<HomeCoreResponse> call, Response<HomeCoreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("HomeViewModel", "getHomeCore: SUCCESS - Loaded from server.");
                    coreData.setValue(UiState.success(response.body()));
                } else {
                    android.util.Log.w("HomeViewModel", "getHomeCore: FAIL (code " + response.code() + ") - Loading fake data.");
                    // Fallback to mock data on non-existent or error endpoint
                    coreData.setValue(UiState.success(getMockHomeCoreResponse()));
                }
            }

            @Override
            public void onFailure(Call<HomeCoreResponse> call, Throwable t) {
                android.util.Log.e("HomeViewModel", "getHomeCore: FAILURE (" + t.getMessage() + ") - Loading fake data.");
                // Fallback to mock data on connection failure
                coreData.setValue(UiState.success(getMockHomeCoreResponse()));
            }
        });
    }

    private void fetchBrandsData() {
        brandsData.setValue(UiState.loading(null));
        apiService.getPopularBrands(currentAddressId).enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("HomeViewModel", "getPopularBrands: SUCCESS - Loaded from server.");
                    if (response.body().getBrands() == null || response.body().getBrands().isEmpty()) {
                        brandsData.setValue(UiState.empty());
                    } else {
                        brandsData.setValue(UiState.success(response.body()));
                    }
                } else {
                    android.util.Log.w("HomeViewModel", "getPopularBrands: FAIL (code " + response.code() + ") - Loading fake data.");
                    // Fallback to mock brands
                    brandsData.setValue(UiState.success(getMockBrandResponse()));
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                android.util.Log.e("HomeViewModel", "getPopularBrands: FAILURE (" + t.getMessage() + ") - Loading fake data.");
                // Fallback to mock brands
                brandsData.setValue(UiState.success(getMockBrandResponse()));
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
                    android.util.Log.w("HomeViewModel", "getRecommendedDeals (page " + currentDealsPage + "): FAIL (code " + response.code() + ") - Loading fake data.");
                    // Fallback to mock deals
                    List<RecommendedDealModel> mockDeals = FakeDealGenerator.generateDeals(10, currentTabId);
                    accumulatedDeals.addAll(mockDeals);
                    dealsData.setValue(UiState.success(new ArrayList<>(accumulatedDeals)));
                    if (currentDealsPage >= 5) {
                        hasMoreDeals = false;
                    }
                }
            }

            @Override
            public void onFailure(Call<DealResponse> call, Throwable t) {
                isDealsLoading = false;
                android.util.Log.e("HomeViewModel", "getRecommendedDeals (page " + currentDealsPage + "): FAILURE (" + t.getMessage() + ") - Loading fake data.");
                // Fallback to mock deals
                List<RecommendedDealModel> mockDeals = FakeDealGenerator.generateDeals(10, currentTabId);
                accumulatedDeals.addAll(mockDeals);
                dealsData.setValue(UiState.success(new ArrayList<>(accumulatedDeals)));
                if (currentDealsPage >= 5) {
                    hasMoreDeals = false;
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
