package com.uit.fooddelivery_api.modules.food.services;

import com.uit.fooddelivery_api.modules.food.dtos.CreateFoodDTO;
import com.uit.fooddelivery_api.modules.food.dtos.FoodDetailResponseDTO;
import com.uit.fooddelivery_api.modules.food.entities.Category;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.entities.FoodOptionGroup;
import com.uit.fooddelivery_api.modules.food.repositories.CategoryRepository;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.food.repositories.FoodOptionGroupRepository;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final FoodOptionGroupRepository foodOptionGroupRepository;
    private final com.uit.fooddelivery_api.modules.user.services.CloudinaryService cloudinaryService;

    // Hàm xử lý upload ảnh món ăn
    @jakarta.transaction.Transactional
    public String updateFoodImage(Long foodId, org.springframework.web.multipart.MultipartFile file, User merchant) throws java.io.IOException {
        // 1. Tìm món ăn xem có tồn tại không
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn!"));

        // 2. CHECK BẢO MẬT: Thằng tài khoản này có đúng là chủ của nhà hàng chứa món ăn này không?
        if (!food.getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Anh không có quyền đổi ảnh món ăn của nhà hàng khác!");
        }

        // 3. Bắn ảnh lên Cloudinary lấy link về
        String imageUrl = cloudinaryService.uploadAvatar(file);

        // 4. Cập nhật link ảnh vào món ăn và lưu lại
        food.setImageUrl(imageUrl);
        foodRepository.save(food);

        return imageUrl;
    }

    public Food createFood(CreateFoodDTO dto, User merchant) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng!"));

        // 2. CHECK BẢO MẬT: Ông đang đăng nhập có đúng là chủ của nhà hàng này không?
        if (!restaurant.getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Anh không có quyền thêm món ăn vào nhà hàng của người khác!");
        }

        // 3. Kiểm tra danh mục món ăn (nếu có truyền lên)
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục món ăn!"));
        }

        // 4. Khởi tạo món ăn mới
        Food food = Food.builder()
                .restaurant(restaurant)
                .category(category)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .isAvailable(true)
                .build();

        return foodRepository.save(food);
    }

    // Lấy danh sách món ăn theo nhà hàng (public - không cần đăng nhập - chỉ lấy món đang bán)
    public List<Food> getFoodsByRestaurant(Long restaurantId) {
        return foodRepository.findByRestaurantId(restaurantId).stream()
                .filter(food -> Boolean.TRUE.equals(food.getIsAvailable()))
                .toList();
    }

    // Lấy tất cả món ăn của nhà hàng (bao gồm cả món đang ngưng bán - dành cho chủ quán)
    public List<Food> getAllFoodsByRestaurant(Long restaurantId) {
        return foodRepository.findByRestaurantId(restaurantId);
    }

    // Lấy danh sách món ăn theo danh mục (public - không cần đăng nhập)
    public List<Food> getFoodsByCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục món ăn!"));
        return foodRepository.findByCategoryIdAndIsAvailableTrue(categoryId);
    }

    // Lấy chi tiết 1 món ăn theo ID (public - không cần đăng nhập)
    public Food getFoodById(Long foodId) {
        return foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn với id: " + foodId));
    }

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

    // Lấy tất cả món ăn đang bán (public - không cần đăng nhập)
    public List<com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO> getAllAvailableFoods(Double lat, Double lng) {
        return foodRepository.findAll().stream()
                .filter(food -> Boolean.TRUE.equals(food.getIsAvailable()))
                .map(food -> {
                    com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO dto = com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO.fromEntity(food);
                    if (lat != null && lng != null && food.getRestaurant() != null && food.getRestaurant().getLatitude() != null && food.getRestaurant().getLongitude() != null) {
                        double distance = calculateDistance(lat, lng, food.getRestaurant().getLatitude().doubleValue(), food.getRestaurant().getLongitude().doubleValue());
                        distance = Math.round(distance * 10.0) / 10.0;
                        dto.setDistance(distance);
                    }
                    return dto;
                })
                .toList();
    }

    // API Cập nhật thông tin món ăn
    @jakarta.transaction.Transactional
    public Food updateFood(Long foodId, CreateFoodDTO dto, User merchant) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn!"));

        // Check quyền chủ quán
        if (!food.getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Anh không có quyền sửa món ăn của nhà hàng khác!");
        }

        food.setName(dto.getName());
        food.setDescription(dto.getDescription());
        food.setPrice(dto.getPrice());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục món ăn!"));
            food.setCategory(category);
        }

        return foodRepository.save(food);
    }

    // API Xóa món ăn (Xóa mềm - Ngưng bán)
    @jakarta.transaction.Transactional
    public void deleteFood(Long foodId, User merchant) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn!"));

        // Check quyền chủ quán
        if (!food.getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Anh không có quyền xóa món ăn của nhà hàng khác!");
        }

        // Chuyển trạng thái thành ngưng bán thay vì xóa data để giữ lịch sử hóa đơn
        food.setIsAvailable(false);
        foodRepository.save(food);
    }

    // API Cập nhật trạng thái món ăn (Bật/Tắt)
    @jakarta.transaction.Transactional
    public void updateFoodStatus(Long foodId, Boolean isAvailable, User merchant) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn!"));

        // Check quyền chủ quán
        if (!food.getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Anh không có quyền sửa món ăn của nhà hàng khác!");
        }

        food.setIsAvailable(isAvailable);
        foodRepository.save(food);
    }

    public FoodDetailResponseDTO getFoodDetailById(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn với id: " + foodId));
        List<FoodOptionGroup> optionGroups = foodOptionGroupRepository.findByFoodId(foodId);
        return FoodDetailResponseDTO.fromEntity(food, optionGroups);
    }

    public List<com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO> searchFoodsByKeyword(String keyword, Double lat, Double lng) {
        String cleanKeyword = keyword.trim();
        String booleanKeyword = java.util.Arrays.stream(cleanKeyword.split("\\s+"))
                .filter(w -> !w.isEmpty())
                .map(word -> "+" + word)
                .collect(java.util.stream.Collectors.joining(" "));
        if (booleanKeyword.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO> results = foodRepository.searchFoodsByKeyword(booleanKeyword, cleanKeyword).stream().map(food -> {
            com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO dto = com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO.fromEntity(food);
            if (lat != null && lng != null && food.getRestaurant() != null && food.getRestaurant().getLatitude() != null && food.getRestaurant().getLongitude() != null) {
                double distance = calculateDistance(lat, lng, food.getRestaurant().getLatitude().doubleValue(), food.getRestaurant().getLongitude().doubleValue());
                distance = Math.round(distance * 10.0) / 10.0;
                dto.setDistance(distance);
            } else {
                dto.setDistance(0.0); // Default distance
            }
            return dto;
        }).collect(java.util.stream.Collectors.toList()); // Collect to a mutable list

        // Fallback: Fuzzy search in Java if DB returns empty or if we want to augment results
        if (results.isEmpty()) {
            List<com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO> allFoods = getAllAvailableFoods(lat, lng);
            for (com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO f : allFoods) {
                if (f.getDistance() != null && f.getDistance() <= 20.0 && isFuzzyMatch(f.getName(), cleanKeyword)) {
                    results.add(f);
                }
            }
        }

        // Sort primarily by distance ASC
        results.sort((f1, f2) -> {
            Double d1 = f1.getDistance() != null ? f1.getDistance() : 0.0;
            Double d2 = f2.getDistance() != null ? f2.getDistance() : 0.0;
            return Double.compare(d1, d2);
        });

        return results;
    }

    private String removeAccents(String str) {
        if (str == null) return "";
        String nfdNormalizedString = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private boolean isFuzzyMatch(String text, String keyword) {
        if (text == null || keyword == null) return false;
        String textNoAccent = removeAccents(text).toLowerCase();
        String kwNoAccent = removeAccents(keyword).toLowerCase();
        
        if (textNoAccent.contains(kwNoAccent)) return true;

        String[] textWords = textNoAccent.split("\\s+");
        String[] keyWords = kwNoAccent.split("\\s+");
        for (String kw : keyWords) {
            boolean wordMatched = false;
            for (String tw : textWords) {
                if (tw.contains(kw) || calculateLevenshtein(tw, kw) <= 1) {
                    wordMatched = true;
                    break;
                }
            }
            if (!wordMatched) return false;
        }
        return true;
    }

    private int calculateLevenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1), 
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        return dp[a.length()][b.length()];
    }
}