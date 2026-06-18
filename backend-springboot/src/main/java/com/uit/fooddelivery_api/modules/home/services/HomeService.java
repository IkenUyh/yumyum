package com.uit.fooddelivery_api.modules.home.services;

import com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem;
import com.uit.fooddelivery_api.modules.flashsale.repositories.FlashSaleItemRepository;
import com.uit.fooddelivery_api.modules.food.entities.Category;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.repositories.CategoryRepository;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.home.dtos.*;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

        private final RestaurantRepository restaurantRepository;
        private final FoodRepository foodRepository;
        private final CategoryRepository categoryRepository;
        private final FlashSaleItemRepository flashSaleItemRepository;
        private final Random random = new Random();

        public HomeCoreResponseDTO getHomeCore(String addressId) {
                // 1. Banners
                List<BannerDTO> banners = List.of(
                                BannerDTO.builder().id("b1").imageUrl("img_priority_banner1").link("").build(),
                                BannerDTO.builder().id("b2").imageUrl("img_priority_banner2").link("").build(),
                                BannerDTO.builder().id("b3").imageUrl("img_priority_banner3").link("").build());

                // 2. Categories
                List<Category> dbCategories = categoryRepository.findAll();
                List<FoodCategoryDTO> categories = dbCategories.stream()
                                .map(c -> FoodCategoryDTO.builder()
                                                .name(c.getName())
                                                .bgColor(0) // dynamic fallback on frontend
                                                .iconResId(0) // dynamic fallback on frontend
                                                .isSelectAll(false)
                                                .build())
                                .collect(Collectors.toList());

                // Add "Danh mục" at the end if list is not empty
                categories.add(FoodCategoryDTO.builder()
                                .name("Danh mục")
                                .bgColor(0)
                                .iconResId(0)
                                .isSelectAll(true)
                                .build());

                // 3. Flashsales
                List<FlashSaleItem> activeFS = flashSaleItemRepository.findActiveFlashSaleItems(LocalDateTime.now());
                // Lấy tất cả Food cùng Restaurant (chỉ gọi 1 lần để dùng chung)
                List<Food> allFoodsWithRestaurant = foodRepository.findAllWithRestaurant();

                List<FoodMenuItemDTO> flashSales = new ArrayList<>();
                if (!activeFS.isEmpty()) {
                        flashSales = activeFS.stream()
                                        .map(fs -> FoodMenuItemDTO.builder()
                                                        .id("fs_" + fs.getFood().getId())
                                                        .name(fs.getFood().getName())
                                                        .price(fs.getSalePrice().longValue())
                                                        .imageResId(0)
                                                        .description(fs.getFood().getDescription())
                                                        .build())
                                        .limit(5)
                                        .collect(Collectors.toList());
                } else {
                        // Fallback: use first 3 available foods from database
                        flashSales = allFoodsWithRestaurant.stream()
                                        .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                                        .map(f -> FoodMenuItemDTO.builder()
                                                        .id("f_" + f.getId())
                                                        .name(f.getName())
                                                        .price(f.getPrice().multiply(BigDecimal.valueOf(0.5))
                                                                        .longValue()) // 50% off for flashsale
                                                        .imageResId(0)
                                                        .description(f.getDescription())
                                                        .build())
                                        .limit(3)
                                        .collect(Collectors.toList());
                }

                // 4. Topics
                List<FoodMenuItemDTO> topic1Items = allFoodsWithRestaurant.stream()
                                .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                                .map(f -> FoodMenuItemDTO.builder()
                                                .id("t1_" + f.getId())
                                                .name(f.getName())
                                                .price(f.getPrice().longValue())
                                                .imageResId(0)
                                                .description(f.getDescription())
                                                .build())
                                .limit(4)
                                .collect(Collectors.toList());

                List<FoodMenuItemDTO> topic2Items = allFoodsWithRestaurant.stream()
                                .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                                .skip(4)
                                .map(f -> FoodMenuItemDTO.builder()
                                                .id("t2_" + f.getId())
                                                .name(f.getName())
                                                .price(f.getPrice().longValue())
                                                .imageResId(0)
                                                .description(f.getDescription())
                                                .build())
                                .limit(4)
                                .collect(Collectors.toList());

                List<TopicResponseDTO> topics = List.of(
                                TopicResponseDTO.builder()
                                                .title("Món Ngon Gần Bạn")
                                                .subtitle("Khám phá ẩm thực xung quanh bạn")
                                                .items(topic1Items)
                                                .build(),
                                TopicResponseDTO.builder()
                                                .title("Ưu Đãi Hôm Nay")
                                                .subtitle("Khuyến mãi cực hot dành riêng cho bạn")
                                                .items(topic2Items)
                                                .build());

                return HomeCoreResponseDTO.builder()
                                .banners(banners)
                                .categories(categories)
                                .flashSales(flashSales)
                                .topics(topics)
                                .build();
        }

        public BrandResponseDTO getPopularBrands(String addressId) {
                // Tránh O(N^2) N+1 query: Lấy tất cả Food cùng Restaurant rồi group by
                // Restaurant
                List<Food> allFoods = foodRepository.findAllWithRestaurant();
                java.util.Map<Restaurant, List<Food>> foodsByRestaurant = allFoods.stream()
                                .filter(f -> f.getRestaurant() != null)
                                .collect(Collectors.groupingBy(Food::getRestaurant));

                List<RestaurantHomeDTO> brands = foodsByRestaurant.entrySet().stream()
                                .map(entry -> {
                                        Restaurant r = entry.getKey();
                                        List<Food> foods = entry.getValue();
                                        List<FoodMenuItemDTO> menu = foods.stream()
                                                        .map(f -> FoodMenuItemDTO.builder()
                                                                        .id("rm_" + f.getId())
                                                                        .name(f.getName())
                                                                        .price(f.getPrice().longValue())
                                                                        .imageResId(0)
                                                                        .description(f.getDescription())
                                                                        .build())
                                                        .limit(4)
                                                        .collect(Collectors.toList());

                                        return RestaurantHomeDTO.builder()
                                                        .name(r.getName())
                                                        .shortName(r.getName().split(" ")[0])
                                                        .bgColor(0)
                                                        .category("Ẩm thực")
                                                        .menu(menu)
                                                        .imageResId(0)
                                                        .rating(r.getRatingAverage() != null
                                                                        ? r.getRatingAverage().doubleValue()
                                                                        : 4.5)
                                                        .reviewCount(r.getReviewCount() != null ? r.getReviewCount()
                                                                        : 100)
                                                        .deliveryTime(20 + random.nextInt(20))
                                                        .address(r.getAddress())
                                                        .build();
                                })
                                .limit(8)
                                .collect(Collectors.toList());

                return BrandResponseDTO.builder().brands(brands).build();
        }

        public DealResponseDTO getRecommendedDeals(String addressId, int tabId, int page, int size) {
                List<Food> allFoods = foodRepository.findAllWithRestaurant();
                if (allFoods.isEmpty()) {
                        return DealResponseDTO.builder()
                                        .deals(new ArrayList<>())
                                        .totalPages(0)
                                        .currentPage(page)
                                        .build();
                }

                List<RecommendedDealDTO> deals = allFoods.stream()
                                .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                                .map(f -> {
                                        double distance = 0.5 + random.nextDouble() * 5.0;
                                        distance = Math.round(distance * 10.0) / 10.0;
                                        int delTime = (int) (distance * 4 + 10);
                                        double origPrice = f.getPrice().doubleValue();
                                        double discPrice = origPrice * 0.8;

                                        return RecommendedDealDTO.builder()
                                                        .storeName(f.getRestaurant().getName())
                                                        .distance(distance)
                                                        .deliveryTime(delTime)
                                                        .foodImageResId(0)
                                                        .discountTag("-20%")
                                                        .foodTitle(f.getName())
                                                        .soldCount(10 + random.nextInt(200))
                                                        .originalPrice(origPrice)
                                                        .discountPrice(discPrice)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // Simple in-memory pagination
                int fromIndex = (page - 1) * size;
                if (fromIndex >= deals.size()) {
                        return DealResponseDTO.builder()
                                        .deals(new ArrayList<>())
                                        .totalPages((int) Math.ceil((double) deals.size() / size))
                                        .currentPage(page)
                                        .build();
                }
                int toIndex = Math.min(fromIndex + size, deals.size());
                List<RecommendedDealDTO> pagedList = deals.subList(fromIndex, toIndex);

                return DealResponseDTO.builder()
                                .deals(pagedList)
                                .totalPages((int) Math.ceil((double) deals.size() / size))
                                .currentPage(page)
                                .build();
        }
}
