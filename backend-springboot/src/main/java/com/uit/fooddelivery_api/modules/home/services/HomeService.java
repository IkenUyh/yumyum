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
                BannerDTO.builder().id("b3").imageUrl("img_priority_banner3").link("").build()
        );

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
            List<Food> allFoods = foodRepository.findAll();
            flashSales = allFoods.stream()
                    .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                    .map(f -> FoodMenuItemDTO.builder()
                            .id("f_" + f.getId())
                            .name(f.getName())
                            .price(f.getPrice().multiply(BigDecimal.valueOf(0.5)).longValue()) // 50% off for flashsale
                            .imageResId(0)
                            .description(f.getDescription())
                            .build())
                    .limit(3)
                    .collect(Collectors.toList());
        }

        // 4. Topics (Randomly pick 2 categories and show their foods)
        List<Food> allFoods = foodRepository.findAll();
        List<Category> randomCategories = new ArrayList<>(dbCategories);
        java.util.Collections.shuffle(randomCategories);
        
        List<TopicResponseDTO> topics = new ArrayList<>();
        
        for (Category category : randomCategories) {
            if (topics.size() >= 2) break;
            
            List<FoodMenuItemDTO> items = allFoods.stream()
                    .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                    .filter(f -> f.getCategory() != null && f.getCategory().getId().equals(category.getId()))
                    .map(f -> FoodMenuItemDTO.builder()
                            .id("t" + topics.size() + "_" + f.getId())
                            .name(f.getName())
                            .price(f.getPrice().longValue())
                            .imageResId(0)
                            .description(f.getDescription())
                            .build())
                    .limit(4)
                    .collect(Collectors.toList());
                    
            if (!items.isEmpty()) {
                String rawName = category.getName();
                String niceName = getNiceCategoryName(rawName);
                String subtitle = getCategorySubtitle(niceName);

                topics.add(TopicResponseDTO.builder()
                        .title(niceName)
                        .subtitle(subtitle)
                        .items(items)
                        .build());
            }
        }
        
        // Fallback if we don't have enough categories with foods
        if (topics.isEmpty()) {
            topics.add(TopicResponseDTO.builder()
                    .title("Món Ngon Gần Bạn")
                    .subtitle("Khám phá ẩm thực xung quanh bạn")
                    .items(new ArrayList<>())
                    .build());
        }
        if (topics.size() == 1) {
            topics.add(TopicResponseDTO.builder()
                    .title("Ưu Đãi Hôm Nay")
                    .subtitle("Khuyến mãi cực hot dành riêng cho bạn")
                    .items(new ArrayList<>())
                    .build());
        }

        return HomeCoreResponseDTO.builder()
                .banners(banners)
                .categories(categories)
                .flashSales(flashSales)
                .topics(topics)
                .build();
    }

    public BrandResponseDTO getPopularBrands(String addressId) {
        List<Restaurant> dbRestaurants = restaurantRepository.findAll();
        List<RestaurantHomeDTO> brands = dbRestaurants.stream()
                .map(r -> {
                    List<Food> foods = foodRepository.findByRestaurantId(r.getId());
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
                            .rating(r.getRatingAverage() != null ? r.getRatingAverage().doubleValue() : 4.5)
                            .reviewCount(r.getReviewCount() != null ? r.getReviewCount() : 100)
                            .deliveryTime(20 + random.nextInt(20))
                            .address(r.getAddress())
                            .build();
                })
                .limit(8)
                .collect(Collectors.toList());

        return BrandResponseDTO.builder().brands(brands).build();
    }

    public DealResponseDTO getRecommendedDeals(String addressId, int tabId, int page, int size) {
        List<Food> allFoods = foodRepository.findAll();
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

    private String getNiceCategoryName(String rawName) {
        if (rawName == null) return "Món Ngon";
        String lower = rawName.toLowerCase();
        if (lower.contains("bun") || lower.contains("pho") || lower.contains("hutieu")) return "Bún - Phở - Hủ Tiếu";
        if (lower.contains("banhmi") || lower.contains("banh mi")) return "Bánh Mì";
        if (lower.contains("dochay")) return "Đồ Chay";
        if (lower.contains("mianlien")) return "Mì Ăn Liền";
        if (lower.contains("nuoc ngot")) return "Nước Giải Khát";
        if (lower.contains("fastfood")) return "Thức Ăn Nhanh";
        if (lower.contains("bia")) return "Bia - Rượu";
        if (lower.contains("tea")) return "Trà";
        if (lower.contains("sua")) return "Sữa";
        if (lower.contains("cafe")) return "Cà Phê";
        if (lower.contains("thuc an khac")) return "Món Ngon Khác";
        if (lower.contains("cuon") || lower.contains("salad") || lower.contains("goi")) return "Cuốn - Gỏi - Salad";
        if (lower.contains("nuoc ep") || lower.contains("sinh to")) return "Nước Ép - Sinh Tố";
        return rawName;
    }

    private String getCategorySubtitle(String niceName) {
        switch (niceName) {
            case "Bún - Phở - Hủ Tiếu": return "Tinh hoa ẩm thực Việt Nam";
            case "Bánh Mì": return "Giòn rụm, nhân đầy ắp";
            case "Đồ Chay": return "Thanh đạm, tốt cho sức khỏe";
            case "Mì Ăn Liền": return "Nhanh gọn, tiện lợi";
            case "Nước Giải Khát": return "Sảng khoái tức thì";
            case "Thức Ăn Nhanh": return "Giòn rụm, ngon mê ly";
            case "Bia - Rượu": return "Thức uống cho những dịp đặc biệt";
            case "Trà": return "Đậm vị trà, thơm hương tự nhiên";
            case "Cà Phê": return "Khởi đầu ngày mới đầy năng lượng";
            case "Sữa": return "Bổ sung năng lượng mỗi ngày";
            case "Cuốn - Gỏi - Salad": return "Tươi xanh, nhẹ bụng";
            case "Nước Ép - Sinh Tố": return "Tươi mát, nạp vitamin mỗi ngày";
            case "Cơm": return "Chắc bụng, đậm đà hương vị";
            case "Lẩu": return "Nước lẩu đậm đà, đồ nhúng thả ga";
            case "Hải sản": return "Hương vị biển cả mang đến tận bàn";
            case "Đồ nướng": return "Thịt nướng xèo xèo, thơm nức mũi";
            case "Cháo": return "Dễ tiêu, bổ dưỡng, thơm ngon";
            case "Bánh ngọt": return "Ngọt ngào từng khoảnh khắc";
            case "Bánh bao": return "Vỏ mềm xốp, nhân đậm đà";
            case "Ăn vặt": return "Nhai vui miệng, lai rai cả ngày";
            case "Xôi": return "Hạt xôi dẻo thơm, ấm lòng ngày mới";
            default: return "Khám phá các món ngon nhất hôm nay";
        }
    }
}
