package com.example.uitpayapp.home;

import com.example.uitpayapp.R;
import com.example.uitpayapp.recommendeddeal.RecommendedDealModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates fake deal data for infinite scroll.
 * Simulates pagination with random combinations of store names, food titles, prices, etc.
 */
public class FakeDealGenerator {

    private static final Random random = new Random();

    private static final String[] STORE_NAMES = {
            "Gà Rán Popeyes - Võ Văn Ngân",
            "The Coffee House - Kha Vạn Cân",
            "Phúc Long Tea & Coffee",
            "KFC - Đặng Văn Bi",
            "Jollibee - Phạm Văn Đồng",
            "Highlands Coffee - Thủ Đức",
            "Texas Chicken - Linh Trung",
            "Burger King - Gigamall",
            "MAYCHA - Trà Sữa Thủ Đức",
            "Domino's Pizza - Võ Văn Ngân",
            "Bún Bò Huế O Xuân - Q.Thủ Đức",
            "Phở 24 - Kha Vạn Cân",
            "Cơm Tấm Phúc Lộc Thọ",
            "Bánh Mì Huynh Hoa - CN2",
            "Ông Bầu Coffee - ĐHQG",
            "Tous Les Jours - Vincom",
            "Lotteria - Thủ Đức",
            "Pizza Hut - Gigamall",
            "Gà Rán Ông Già - Linh Trung",
            "Trà Sữa Bobapop - Kha Vạn Cân"
    };

    private static final String[] FOOD_TITLES = {
            "1 MIẾNG GÀ RÁN GIÒN + 1 GÀ POPCORN",
            "Trà Đào Cam Sả (L) + Bánh Mì Que",
            "Trà Sữa Phúc Long + Thạch Cafe",
            "Combo Gà Rán Hạnh Phúc",
            "Burger Bò Phô Mai Đặc Biệt",
            "Cơm Gà Sốt Teriyaki",
            "Pizza Hải Sản Size M",
            "Trà Sen Vàng + Bánh Flan",
            "Bún Bò Huế Đặc Biệt",
            "Phở Bò Tái Nạm",
            "Cơm Tấm Sườn Bì Chả",
            "Bánh Mì Thịt Nguội Đặc Biệt",
            "Combo 2 Miếng Gà + Khoai + Nước",
            "Freeze Trà Xanh + Bánh Croissant",
            "Trà Sữa Matcha Trân Châu Đen",
            "Mì Ý Sốt Bò Bằm"
    };

    private static final String[] DISCOUNT_TAGS = {
            "-15%", "-20%", "-25%", "-30%", "-35%", "-40%", "-50%", "-52%"
    };

    private static final int[] FOOD_IMAGES = {
            R.drawable.img_food_chicken,
            R.drawable.img_food_bubbletea,
            R.drawable.img_food_coffee,
            R.drawable.img_food_pizza
    };

    /**
     * Generate a batch of fake deals.
     * @param count number of deals to generate
     * @param tabIndex 0=all, 1=best sellers (high sold count), 2=near me (low distance)
     * @return list of generated deals
     */
    public static List<RecommendedDealModel> generateDeals(int count, int tabIndex) {
        List<RecommendedDealModel> deals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String store = STORE_NAMES[random.nextInt(STORE_NAMES.length)];
            String food = FOOD_TITLES[random.nextInt(FOOD_TITLES.length)];
            String discount = DISCOUNT_TAGS[random.nextInt(DISCOUNT_TAGS.length)];
            int image = FOOD_IMAGES[random.nextInt(FOOD_IMAGES.length)];

            double distance;
            long soldCount;
            int deliveryTime;

            switch (tabIndex) {
                case 1: // Bán chạy — high sold count
                    soldCount = 200 + random.nextInt(800);
                    distance = 0.5 + random.nextDouble() * 15;
                    break;
                case 2: // Gần tôi — low distance
                    distance = 0.1 + random.nextDouble() * 3;
                    soldCount = 10 + random.nextInt(300);
                    break;
                default: // Tất cả
                    distance = 0.3 + random.nextDouble() * 12;
                    soldCount = 10 + random.nextInt(500);
                    break;
            }

            distance = Math.round(distance * 10.0) / 10.0;
            deliveryTime = (int) (distance * 4 + 5 + random.nextInt(10));

            double originalPrice = (30 + random.nextInt(170)) * 1000.0;
            double discountPrice = originalPrice * (0.5 + random.nextDouble() * 0.35);
            discountPrice = Math.round(discountPrice / 1000.0) * 1000.0;

            deals.add(new RecommendedDealModel(
                    store, distance, deliveryTime, image,
                    discount, food, soldCount, originalPrice, discountPrice
            ));
        }
        return deals;
    }
}
