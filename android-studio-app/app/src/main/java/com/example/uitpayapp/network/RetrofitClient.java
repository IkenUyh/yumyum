package com.example.uitpayapp.network;

import com.example.uitpayapp.modules.cart.CartService;
import com.example.uitpayapp.modules.chat.ChatService;
import com.example.uitpayapp.modules.food.FoodService;
import com.example.uitpayapp.modules.merchant.MerchantService;
import com.example.uitpayapp.modules.order.OrderService;
import com.example.uitpayapp.modules.review.ReviewService;
import com.example.uitpayapp.modules.statistic.StatisticService;
import com.example.uitpayapp.modules.system.SystemParameterService;
import com.example.uitpayapp.modules.user.AddressService;
import com.example.uitpayapp.modules.user.DriverService; // Thêm import này
import com.example.uitpayapp.modules.user.UserService;
import com.example.uitpayapp.modules.wallet.WalletService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://kienhuy-dev.name.vn/";
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static UserService getUserService() {
        return getClient().create(UserService.class);
    }

    public static AddressService getAddressService() {
        return getClient().create(AddressService.class);
    }

    // THÊM MỚI: Đăng ký dịch vụ định vị tài xế
    public static DriverService getDriverService() {
        return getClient().create(DriverService.class);
    }

    public static WalletService getWalletService() {
        return getClient().create(WalletService.class);
    }

    public static SystemParameterService getSystemParameterService() {
        return getClient().create(SystemParameterService.class);
    }

    public static com.example.uitpayapp.modules.statistic.StatisticService getStatisticService() {
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

    public static CartService getCartService() {
        return getClient().create(CartService.class);
    }

    public static ChatService getChatService() {
        return getClient().create(ChatService.class);
    }
}