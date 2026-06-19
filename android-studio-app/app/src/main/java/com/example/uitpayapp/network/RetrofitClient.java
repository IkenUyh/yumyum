package com.example.uitpayapp.network;

import android.content.Context;

import com.example.uitpayapp.modules.cart.CartService;
import com.example.uitpayapp.modules.chat.ChatService;
import com.example.uitpayapp.modules.food.FoodService;
import com.example.uitpayapp.modules.loyalty.LoyaltyService;
import com.example.uitpayapp.modules.merchant.MerchantService;
import com.example.uitpayapp.modules.news.NewsService;
import com.example.uitpayapp.modules.notification.NotificationService;
import com.example.uitpayapp.modules.order.OrderService;
import com.example.uitpayapp.modules.restaurant.RestaurantSearchService;
import com.example.uitpayapp.modules.restaurant.RestaurantService;
import com.example.uitpayapp.modules.review.ReviewService;
import com.example.uitpayapp.modules.statistic.StatisticService;
import com.example.uitpayapp.modules.system.SystemParameterService;
import com.example.uitpayapp.modules.user.AddressService;
import com.example.uitpayapp.modules.user.DriverService;
import com.example.uitpayapp.modules.user.UserService;
import com.example.uitpayapp.modules.wallet.WalletService;
import com.example.uitpayapp.modules.grouporder.GroupOrderService;
import com.example.uitpayapp.modules.favorite.FavoriteService;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.uitpayapp.BuildConfig;

public class RetrofitClient {
    // Tự động lấy URL dựa trên Build Variant (Debug: local, Release: server)
    // Cấu hình ở trong file app/build.gradle
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static Retrofit retrofit = null;
    private static Context appContext = null; // Lưu trữ context toàn cục kích thước nhỏ gọn

    public static String getBaseUrl() {
        return BASE_URL;
    }

    // THÊM MỚI: Hàm khởi tạo một lần duy nhất khi chạy App
    public static void initialize(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext(); // Sử dụng ApplicationContext để chống leak memory
        }
    }

    private static Retrofit getClient() {
        if (retrofit == null) {
            if (appContext == null) {
                throw new IllegalStateException(
                        "RetrofitClient chưa được khởi tạo! Hãy gọi RetrofitClient.initialize(context) trước.");
            }

            // Cấu hình OkHttpClient tự động đính kèm Token
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            String path = originalRequest.url().encodedPath();

                            // Bỏ qua đính kèm Token cho các API public như login, register, forgot-password
                            if (path.contains("/users/login") || path.contains("/users/register")
                                    || path.contains("/users/forgot-password")) {
                                return chain.proceed(originalRequest);
                            }

                            // Lấy token thông qua appContext đã được khởi tạo trước đó
                            SessionManager sessionManager = SessionManager.getInstance(appContext);
                            String token = sessionManager.getAuthToken();

                            if (token != null && !token.isEmpty() && !sessionManager.isTokenExpired(token)) {
                                Request newRequest = originalRequest.newBuilder()
                                        .header("Authorization", "Bearer " + token)
                                        .build();
                                return chain.proceed(newRequest);
                            }
                            return chain.proceed(originalRequest);
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // === Quay trở lại hàm KHÔNG THAM SỐ như cũ - Xóa sạch lỗi compile cũ của bạn
    // ===

    public static UserService getUserService() {
        return getClient().create(UserService.class);
    }

    public static AddressService getAddressService() {
        return getClient().create(AddressService.class);
    }

    public static DriverService getDriverService() {
        return getClient().create(DriverService.class);
    }

    public static WalletService getWalletService() {
        return getClient().create(WalletService.class);
    }

    public static SystemParameterService getSystemParameterService() {
        return getClient().create(SystemParameterService.class);
    }

    public static StatisticService getStatisticService() {
        return getClient().create(StatisticService.class);
    }

    public static ReviewService getReviewService() {
        return getClient().create(ReviewService.class);
    }

    public static OrderService getOrderService() {
        return getClient().create(OrderService.class);
    }

    public static MerchantService getMerchantService() {
        return getClient().create(MerchantService.class);
    }

    public static FoodService getFoodService() {
        return getClient().create(FoodService.class);
    }

    public static com.example.uitpayapp.modules.food.CategoryService getCategoryService() {
        return getClient().create(com.example.uitpayapp.modules.food.CategoryService.class);
    }

    public static CartService getCartService() {
        return getClient().create(CartService.class);
    }

    public static ChatService getChatService() {
        return getClient().create(ChatService.class);
    }

    public static LoyaltyService getLoyaltyService() {
        return getClient().create(LoyaltyService.class);
    }

    public static GroupOrderService getGroupOrderService() {
        return getClient().create(GroupOrderService.class);
    }

    public static NewsService getNewsService() {
        return getClient().create(NewsService.class);
    }

    public static NotificationService getNotificationService() {
        return getClient().create(NotificationService.class);
    }

    public static RestaurantService getRestaurantService() {
        return getClient().create(RestaurantService.class);
    }

    public static RestaurantSearchService getRestaurantSearchService() {
        return getClient().create(RestaurantSearchService.class);
    }

    public static com.example.uitpayapp.home.network.HomeApiService getHomeApiService() {
        return getClient().create(com.example.uitpayapp.home.network.HomeApiService.class);
    }

    public static com.example.uitpayapp.home.network.CategoryApiService getCategoryApiService() {
        return getClient().create(com.example.uitpayapp.home.network.CategoryApiService.class);
    }

    public static FavoriteService getFavoriteService() {
        return getClient().create(FavoriteService.class);
    }
}