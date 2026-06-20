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

    // Lấy danh sách món ăn theo nhà hàng (public - không cần đăng nhập)
    public List<Food> getFoodsByRestaurant(Long restaurantId) {
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

    // Lấy tất cả món ăn đang bán (public - không cần đăng nhập)
    public List<Food> getAllAvailableFoods() {
        return foodRepository.findAll().stream()
                .filter(food -> Boolean.TRUE.equals(food.getIsAvailable()))
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

    public FoodDetailResponseDTO getFoodDetailById(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn với id: " + foodId));
        List<FoodOptionGroup> optionGroups = foodOptionGroupRepository.findByFoodId(foodId);
        return FoodDetailResponseDTO.fromEntity(food, optionGroups);
    }

    public List<Food> searchFoodsByKeyword(String keyword) {
        return foodRepository.searchFoodsByKeyword(keyword);
    }
}