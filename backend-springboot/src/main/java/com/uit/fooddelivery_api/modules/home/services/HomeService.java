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
import com.uit.fooddelivery_api.modules.food.services.PriceCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

        private final RestaurantRepository restaurantRepository;
        private final FoodRepository foodRepository;
        private final CategoryRepository categoryRepository;
        private final FlashSaleItemRepository flashSaleItemRepository;
        private final PriceCalculationService priceCalculationService;
        private final Random random = new Random();

        private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
                final int R = 6371; // Radius of the earth in km
                double latDistance = Math.toRadians(lat2 - lat1);
                double lonDistance = Math.toRadians(lon2 - lon1);
                double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                return R * c; 
        }

        public HomeCoreResponseDTO getHomeCore(String addressId) {
                // 1. Banners
                List<BannerDTO> banners = List.of(
                                BannerDTO.builder().id("b1").imageUrl("https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782014543/img_advertisment1_hocxi6.png").link("").build(),
                                BannerDTO.builder().id("b2").imageUrl("https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782014558/img_advertisment2_f2gda5.png").link("").build(),
                                BannerDTO.builder().id("b3").imageUrl("https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782014648/img_advertisment3_upzkv7.png").link("").build());

                // 2. Categories
                List<Category> dbCategories = categoryRepository.findAll();
                List<FoodCategoryDTO> categories = dbCategories.stream()
                                .map(c -> FoodCategoryDTO.builder()
                                                .id(c.getId())
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
                List<Food> allFoodsWithRestaurant = foodRepository.findAllWithRestaurant();
                Map<Long, PriceCalculationService.PriceResult> priceMap = priceCalculationService.calculateFinalPrices(allFoodsWithRestaurant);

                List<FoodMenuItemDTO> flashSales = new ArrayList<>();
                List<Food> foodsWithFlashSale = allFoodsWithRestaurant.stream()
                                .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                                .filter(f -> {
                                        PriceCalculationService.PriceResult pr = priceMap.get(f.getId());
                                        return pr != null && "FLASHSALE".equals(pr.discountType);
                                })
                                .collect(Collectors.toList());

                if (!foodsWithFlashSale.isEmpty()) {
                        flashSales = foodsWithFlashSale.stream()
                                        .map(f -> {
                                                PriceCalculationService.PriceResult pr = priceMap.get(f.getId());
                                                long salePrice = pr.finalPrice.longValue();
                                                long origPrice = pr.originalPrice.longValue();
                                                int discount = origPrice > 0
                                                        ? (int) Math.round((1.0 - (double) salePrice / origPrice) * 100)
                                                        : 0;
                                                return FoodMenuItemDTO.builder()
                                                        .id("fs_" + f.getId())
                                                        .name(f.getName())
                                                        .price(salePrice)
                                                        .originalPrice(origPrice)
                                                        .discountPercent(discount)
                                                        .discountType(pr.discountType)
                                                        .imageResId(0)
                                                        .imageUrl(f.getImageUrl())
                                                        .description(f.getDescription())
                                                        .restaurantId(f.getRestaurant() != null ? f.getRestaurant().getId() : null)
                                                        .restaurantName(f.getRestaurant() != null ? f.getRestaurant().getName() : null)
                                                        .build();
                                        })
                                        .limit(5)
                                        .collect(Collectors.toList());
                } else {
                        // Fallback: use first 3 available foods from database
                        flashSales = allFoodsWithRestaurant.stream()
                                        .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                                        .map(f -> {
                                                PriceCalculationService.PriceResult pr = priceMap.get(f.getId());
                                                long origPrice = pr != null ? pr.originalPrice.longValue() : f.getPrice().longValue();
                                                long salePrice = pr != null ? pr.finalPrice.longValue() : f.getPrice().multiply(BigDecimal.valueOf(0.5)).longValue();
                                                String discType = pr != null ? pr.discountType : "DEAL 50%";
                                                int discount = origPrice > 0 ? (int) Math.round((1.0 - (double) salePrice / origPrice) * 100) : 50;

                                                return FoodMenuItemDTO.builder()
                                                        .id("f_" + f.getId())
                                                        .name(f.getName())
                                                        .price(salePrice)
                                                        .originalPrice(origPrice)
                                                        .discountPercent(discount)
                                                        .discountType(discType)
                                                        .imageResId(0)
                                                        .imageUrl(f.getImageUrl())
                                                        .description(f.getDescription())
                                                        .restaurantId(f.getRestaurant() != null ? f.getRestaurant().getId() : null)
                                                        .restaurantName(f.getRestaurant() != null ? f.getRestaurant().getName() : null)
                                                        .build();
                                        })
                                        .limit(3)
                                        .collect(Collectors.toList());
                }


                return HomeCoreResponseDTO.builder()
                                .banners(banners)
                                .categories(categories)
                                .flashSales(flashSales)
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
                                                                        .imageUrl(f.getImageUrl())
                                                                        .description(f.getDescription())
                                                                        .restaurantId(f.getRestaurant() != null ? f.getRestaurant().getId() : null)
                                                                        .build())
                                                        .limit(4)
                                                        .collect(Collectors.toList());

                                        return RestaurantHomeDTO.builder()
                                                        .id(r.getId())
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

        public DealResponseDTO getRecommendedDeals(String addressId, int tabId, int page, int size, Double lat, Double lng) {
                List<Food> allFoods = foodRepository.findAllWithRestaurant();
                if (allFoods.isEmpty()) {
                        return DealResponseDTO.builder()
                                        .deals(new ArrayList<>())
                                        .totalPages(0)
                                        .currentPage(page)
                                        .build();
                }

                List<RecommendedDealDTO> deals = allFoods.stream()
                                .distinct()
                                .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                                .map(f -> {
                                        double distance = 0.0;
                                        java.util.Random itemRandom = new java.util.Random(f.getId() != null ? f.getId().hashCode() : 0);
                                        if (lat != null && lng != null && f.getRestaurant() != null && f.getRestaurant().getLatitude() != null && f.getRestaurant().getLongitude() != null) {
                                                distance = calculateDistance(lat, lng, f.getRestaurant().getLatitude().doubleValue(), f.getRestaurant().getLongitude().doubleValue());
                                                distance = Math.round(distance * 10.0) / 10.0;
                                        } else {
                                                distance = 0.5 + itemRandom.nextDouble() * 5.0;
                                                distance = Math.round(distance * 10.0) / 10.0;
                                        }
                                        int delTime = (int) (distance * 4 + 10);
                                        PriceCalculationService.PriceResult pr = priceCalculationService.calculateFinalPrice(f);
                                        double origPrice = pr.originalPrice.doubleValue();
                                        double discPrice = pr.finalPrice.doubleValue();
                                        double rating = 3.5 + itemRandom.nextDouble() * 1.5;
                                        rating = Math.round(rating * 10.0) / 10.0;
                                        
                                        int discount = origPrice > 0 ? (int) Math.round((1.0 - discPrice / origPrice) * 100) : 0;
                                        String discountTag = pr.discountType != null ? pr.discountType : ("-" + discount + "%");

                                        return RecommendedDealDTO.builder()
                                                        .storeName(f.getRestaurant().getName())
                                                        .distance(distance)
                                                        .deliveryTime(delTime)
                                                        .foodImageResId(0)
                                                        .imageUrl(f.getImageUrl())
                                                        .discountTag(discountTag)
                                                        .foodTitle(f.getName())
                                                        .soldCount(10 + itemRandom.nextInt(200))
                                                        .originalPrice(origPrice)
                                                        .discountPrice(discPrice)
                                                        .rating(rating)
                                                        .restaurantId(f.getRestaurant() != null ? f.getRestaurant().getId() : null)
                                                        .foodId(f.getId())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                if (tabId == 0) {
                        deals.sort(java.util.Comparator.comparingDouble(RecommendedDealDTO::getDistance));
                } else if (tabId == 1) {
                        deals.sort(java.util.Comparator.comparingLong(RecommendedDealDTO::getSoldCount).reversed());
                } else if (tabId == 2) {
                        deals.sort(java.util.Comparator.comparingDouble(RecommendedDealDTO::getRating).reversed());
                }

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
