package com.example.uitpayapp.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uitpayapp.home.network.BrandResponse;
import com.example.uitpayapp.home.network.DealResponse;
import com.example.uitpayapp.home.network.HomeApiService;
import com.example.uitpayapp.home.network.HomeCoreResponse;
import com.example.uitpayapp.home.network.TopicResponse;
import com.example.uitpayapp.modules.food.models.responses.CategoryResponse;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;
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
    private final com.example.uitpayapp.home.network.CategoryApiService categoryApiService;

    private final MutableLiveData<UiState<HomeCoreResponse>> coreData = new MutableLiveData<>();
    private final MutableLiveData<UiState<BrandResponse>> brandsData = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<RecommendedDealModel>>> dealsData = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<TopicResponse>>> randomTopicsData = new MutableLiveData<>();

    private int currentDealsPage = 1;
    private int currentTabId = 0;
    private String currentAddressId = "";
    private boolean isDealsLoading = false;
    private boolean hasMoreDeals = true;
    private List<RecommendedDealModel> accumulatedDeals = new ArrayList<>();

    public HomeViewModel() {
        this.apiService = RetrofitClient.getHomeApiService();
        this.categoryApiService = RetrofitClient.getCategoryApiService();
    }

    public LiveData<UiState<HomeCoreResponse>> getCoreData() { return coreData; }
    public LiveData<UiState<BrandResponse>> getBrandsData() { return brandsData; }
    public LiveData<UiState<List<RecommendedDealModel>>> getDealsData() { return dealsData; }
    public LiveData<UiState<List<TopicResponse>>> getRandomTopicsData() { return randomTopicsData; }

    public void setAddressAndRefresh(String addressId) {
        this.currentAddressId = addressId;
        refreshAll();
    }

    public void refreshAll() {
        fetchCoreData();
        fetchBrandsData();
        resetAndFetchDeals(currentTabId);
        fetchRandomTopics();
    }

    /** Gọi từ HomeActivity khi Flashsale hết hạn để tải lại dữ liệu mới */
    public void refreshCoreData() {
        fetchCoreData();
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
                    coreData.setValue(UiState.error("Không kết nối được server (Lỗi: " + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<HomeCoreResponse>> call, Throwable t) {
                android.util.Log.e("HomeViewModel", "getHomeCore: FAILURE (" + t.getMessage() + ") - Showing error.");
                coreData.setValue(UiState.error("Không kết nối được server (" + t.getMessage() + ")", null));
            }
        });
    }

    private void fetchBrandsData() {
        brandsData.setValue(UiState.loading(null));
        RetrofitClient.getRestaurantService().getAllRestaurants().enqueue(new Callback<ApiResponse<List<RestaurantResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RestaurantResponseDTO>>> call, Response<ApiResponse<List<RestaurantResponseDTO>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<RestaurantResponseDTO> dtos = response.body().getData();
                    if (dtos.isEmpty()) {
                        brandsData.setValue(UiState.empty());
                    } else {
                        // Map RestaurantResponseDTO -> Restaurant (giới hạn tối đa 10, random)
                        java.util.Collections.shuffle(dtos);
                        List<com.example.uitpayapp.home.home_models.Restaurant> mappedRestaurants = new ArrayList<>();
                        int limit = Math.min(dtos.size(), 10);
                        for (int i = 0; i < limit; i++) {
                            RestaurantResponseDTO dto = dtos.get(i);
                            String shortName = dto.getName() != null && dto.getName().length() > 0
                                    ? dto.getName().substring(0, 1) : "A";
                            double rating = dto.getRatingAverage() != null ? dto.getRatingAverage() : 0.0;
                            int reviewCount = dto.getReviewCount() != null ? dto.getReviewCount() : 0;
                            mappedRestaurants.add(new com.example.uitpayapp.home.home_models.Restaurant(
                                    dto.getId(), dto.getName(), shortName,
                                    android.graphics.Color.parseColor("#E4002B"), "Danh mục",
                                    new ArrayList<>(), 0,
                                    rating, reviewCount, 30, dto.getAddress(), dto.getImageUrl()));
                        }
                        BrandResponse brandResponse = new BrandResponse();
                        brandResponse.setBrands(mappedRestaurants);
                        brandsData.setValue(UiState.success(brandResponse));
                    }
                } else {
                    android.util.Log.w("HomeViewModel", "getAllRestaurants: FAIL (code " + response.code() + ") - Showing error.");
                    brandsData.setValue(UiState.error("Không kết nối được server (Lỗi: " + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RestaurantResponseDTO>>> call, Throwable t) {
                android.util.Log.e("HomeViewModel", "getAllRestaurants: FAILURE (" + t.getMessage() + ") - Showing error.");
                brandsData.setValue(UiState.error("Không kết nối được server (" + t.getMessage() + ")", null));
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
                .enqueue(new Callback<ApiResponse<DealResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DealResponse>> call, Response<ApiResponse<DealResponse>> response) {
                isDealsLoading = false;
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    android.util.Log.d("HomeViewModel", "getRecommendedDeals (page " + currentDealsPage + "): SUCCESS - Loaded from server.");
                    List<RecommendedDealModel> newDeals = response.body().getData().getDeals();
                    if (newDeals != null) {
                        accumulatedDeals.addAll(newDeals);
                    }
                    if (accumulatedDeals.isEmpty()) {
                        dealsData.setValue(UiState.empty());
                    } else {
                        dealsData.setValue(UiState.success(new ArrayList<>(accumulatedDeals)));
                    }
                    
                    if (currentDealsPage >= response.body().getData().getTotalPages()) {
                        hasMoreDeals = false;
                    }
                } else {
                    android.util.Log.w("HomeViewModel", "getRecommendedDeals (page " + currentDealsPage + "): FAIL (code " + response.code() + ") - Showing error.");
                    if (accumulatedDeals.isEmpty()) {
                        dealsData.setValue(UiState.error("Không kết nối được server (Lỗi: " + response.code() + ")", null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DealResponse>> call, Throwable t) {
                isDealsLoading = false;
                android.util.Log.e("HomeViewModel", "getRecommendedDeals (page " + currentDealsPage + "): FAILURE (" + t.getMessage() + ") - Showing error.");
                if (accumulatedDeals.isEmpty()) {
                    dealsData.setValue(UiState.error("Không kết nối được server (" + t.getMessage() + ")", null));
                }
            }
        });
    }

    private void fetchRandomTopics() {
        randomTopicsData.setValue(UiState.loading(null));
        categoryApiService.getAllCategories().enqueue(new Callback<ApiResponse<List<CategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryResponse>>> call, Response<ApiResponse<List<CategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CategoryResponse> categories = new ArrayList<>(response.body().getData());
                    if (categories.isEmpty()) {
                        randomTopicsData.setValue(UiState.empty());
                        return;
                    }
                    Collections.shuffle(categories);
                    int count = Math.min(2, categories.size());
                    List<CategoryResponse> selected = categories.subList(0, count);
                    fetchFoodsForCategories(selected);
                } else {
                    randomTopicsData.setValue(UiState.error("Không kết nối được server (Lỗi: " + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryResponse>>> call, Throwable t) {
                randomTopicsData.setValue(UiState.error("Không kết nối được server (" + t.getMessage() + ")", null));
            }
        });
    }

    private void fetchFoodsForCategories(List<CategoryResponse> categories) {
        List<TopicResponse> result = new ArrayList<>();
        java.util.concurrent.atomic.AtomicInteger pendingCalls = new java.util.concurrent.atomic.AtomicInteger(categories.size());

        for (int i = 0; i < categories.size(); i++) {
            CategoryResponse cat = categories.get(i);
            final int index = i;
            categoryApiService.getFoodsByCategory(cat.getId()).enqueue(new Callback<ApiResponse<List<FoodResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<FoodResponse>>> call, Response<ApiResponse<List<FoodResponse>>> response) {
                    handleFoodResponse(cat, response, result, pendingCalls);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<FoodResponse>>> call, Throwable t) {
                    handleFoodResponse(cat, null, result, pendingCalls);
                }
            });
        }
    }

    private synchronized void handleFoodResponse(CategoryResponse cat, Response<ApiResponse<List<FoodResponse>>> response, List<TopicResponse> result, java.util.concurrent.atomic.AtomicInteger pendingCalls) {
        if (response != null && response.isSuccessful() && response.body() != null && response.body().getData() != null) {
            List<FoodResponse> foods = new ArrayList<>(response.body().getData());
            if (foods.size() > 10) {
                java.util.Collections.shuffle(foods);
                foods = foods.subList(0, 10);
            }
            List<com.example.uitpayapp.home.home_models.FoodMenuItem> items = new ArrayList<>();
            for (FoodResponse f : foods) {
                com.example.uitpayapp.home.home_models.FoodMenuItem item = new com.example.uitpayapp.home.home_models.FoodMenuItem(
                        "f_" + f.getId(),
                        f.getName(),
                        f.getPrice() != null ? f.getPrice().longValue() : 0,
                        0,
                        f.getDescription() != null ? f.getDescription() : "",
                        f.getImageUrl()
                );
                item.setRestaurantId(f.getRestaurantId());
                item.setRestaurantName(f.getRestaurantName());
                items.add(item);
            }
            if (!items.isEmpty()) {
                result.add(new TopicResponse(cat.getName(), "", cat.getId(), items));
            }
        }
        
        if (pendingCalls.decrementAndGet() == 0) {
            if (result.isEmpty()) {
                randomTopicsData.postValue(UiState.empty());
            } else {
                randomTopicsData.postValue(UiState.success(result));
            }
        }
    }

    private HomeCoreResponse getMockHomeCoreResponse() {
        String json = "{" +
                "  \"banners\": [" +
                "    { \"id\": \"b1\", \"imageUrl\": \"https://dummyimage.com/600x300/ff9900/fff&text=Banner+1\", \"link\": \"\" }," +
                "    { \"id\": \"b2\", \"imageUrl\": \"https://dummyimage.com/600x300/33cc33/fff&text=Banner+2\", \"link\": \"\" }," +
                "    { \"id\": \"b3\", \"imageUrl\": \"https://dummyimage.com/600x300/3366ff/fff&text=Banner+3\", \"link\": \"\" }" +
                "  ]," +
                "  \"categories\": [" +
                "    { \"id\": 1, \"name\": \"Cơm\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_com + ", \"bgColor\": -1748736 }," +
                "    { \"id\": 2, \"name\": \"Bún Phở\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_bun_pho + ", \"bgColor\": -16743281 }," +
                "    { \"id\": 3, \"name\": \"Bánh mì\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_banh_mi + ", \"bgColor\": -4246004 }," +
                "    { \"id\": 4, \"name\": \"Fastfood\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_fastfood + ", \"bgColor\": -3790552 }," +
                "    { \"id\": 5, \"name\": \"Lẩu\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_lau + ", \"bgColor\": -2604267 }," +
                "    { \"id\": 6, \"name\": \"Đồ nướng\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_bbq + ", \"bgColor\": -4777216 }," +
                "    { \"id\": 7, \"name\": \"Cafe\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_ca_phe + ", \"bgColor\": -11651810 }," +
                "    { \"id\": 8, \"name\": \"Trà sữa\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_tra_sua + ", \"bgColor\": -7508125 }," +
                "    { \"id\": 9, \"name\": \"Ăn vặt\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_an_vat + ", \"bgColor\": -9823334 }," +
                "    { \"id\": 10, \"name\": \"Danh mục\", \"iconResId\": " + com.example.uitpayapp.R.drawable.ic_cat_all + ", \"bgColor\": -14142317, \"isSelectAll\": true }" +
                "  ]," +
                "  \"flashSales\": [" +
                "    { \"id\": \"d_1\", \"name\": \"Gà rán truyền thống\", \"price\": 45000, \"imageResId\": " + 0 + ", \"description\": \"1 miếng gà rán giòn\" }," +
                "    { \"id\": \"d_2\", \"name\": \"Combo gà rán + khoai\", \"price\": 89000, \"imageResId\": " + 0 + ", \"description\": \"2 miếng gà + khoai tây\" }," +
                "    { \"id\": \"d_3\", \"name\": \"Burger gà giòn\", \"price\": 39000, \"imageResId\": " + 0 + ", \"description\": \"Burger gà với rau tươi\" }" +
                "  ]" +
                "}";
        return new com.google.gson.Gson().fromJson(json, HomeCoreResponse.class);
    }

    private BrandResponse getMockBrandResponse() {
        java.util.List<com.example.uitpayapp.home.home_models.Restaurant> restaurantsList = new java.util.ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            restaurantsList.add(new com.example.uitpayapp.home.home_models.Restaurant((long) i, "KFC " + i, "KFC", 0, "Gà rán", new java.util.ArrayList<>(), 0, 4.8, 1250, 25, "KFC Võ Văn Ngân"));
        }
        String json = "{" +
                "  \"brands\": " + new com.google.gson.Gson().toJson(restaurantsList) +
                "}";
        return new com.google.gson.Gson().fromJson(json, BrandResponse.class);
    }
}
