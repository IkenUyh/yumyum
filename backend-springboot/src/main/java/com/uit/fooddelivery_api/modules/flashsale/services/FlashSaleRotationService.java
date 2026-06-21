package com.uit.fooddelivery_api.modules.flashsale.services;

import com.uit.fooddelivery_api.modules.flashsale.entities.FlashSale;
import com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem;
import com.uit.fooddelivery_api.modules.flashsale.repositories.FlashSaleRepository;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Service tự động xoay vòng (rotate) các món ăn Flashsale mỗi giờ.
 *
 * <p>Mỗi giờ, service sẽ:
 * <ol>
 *   <li>Vô hiệu hóa tất cả các chiến dịch Flashsale tự động ([AUTO]) đã hết hạn</li>
 *   <li>Chọn ngẫu nhiên 3-5 món ăn đang có sẵn (isAvailable = true)</li>
 *   <li>Tạo một chiến dịch Flashsale mới kéo dài 1 tiếng với giảm giá 30%-50%</li>
 *   <li>Reset số lượng đã bán (soldQuantity) về 0 cho mỗi suất mới</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class FlashSaleRotationService {

    private final FlashSaleRepository flashSaleRepository;
    private final FoodRepository foodRepository;

    /** Số lượng món tối thiểu được chọn cho mỗi đợt Flashsale */
    private static final int MIN_ITEMS = 3;

    /** Số lượng món tối đa được chọn cho mỗi đợt Flashsale */
    private static final int MAX_ITEMS = 5;

    /** Số lượng suất bán mặc định cho mỗi món Flashsale */
    private static final int DEFAULT_STOCK = 50;

    /** Phần trăm giảm giá tối thiểu (30%) */
    private static final double MIN_DISCOUNT_PERCENT = 0.30;

    /** Phần trăm giảm giá tối đa (50%) */
    private static final double MAX_DISCOUNT_PERCENT = 0.50;

    private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm dd/MM");

    /**
     * Thực hiện xoay vòng Flashsale:
     * 1. Vô hiệu hóa Flashsale cũ
     * 2. Chọn món ngẫu nhiên
     * 3. Tạo chiến dịch Flashsale mới (1 tiếng)
     *
     * @return FlashSale mới được tạo, hoặc null nếu không đủ món ăn
     */
    @Transactional
    public FlashSale rotateFlashSale() {
        LocalDateTime now = LocalDateTime.now();

        // ===== BƯỚC 1: Vô hiệu hóa các Flashsale tự động đã hết hạn =====
        int deactivatedCount = flashSaleRepository.deactivateExpiredFlashSales(now);
        if (deactivatedCount > 0) {
            System.out.println("⚡ FlashSaleRotation: Đã vô hiệu hóa " + deactivatedCount + " chiến dịch Flashsale cũ.");
        }

        // Kiểm tra xem đã có Flashsale tự động đang chạy chưa (tránh tạo trùng)
        List<FlashSale> existingAuto = flashSaleRepository.findActiveAutoFlashSales(now);
        if (!existingAuto.isEmpty()) {
            System.out.println("⚡ FlashSaleRotation: Đã có Flashsale tự động đang hoạt động, bỏ qua.");
            return existingAuto.get(0);
        }

        // ===== BƯỚC 2: Lấy danh sách món ăn có sẵn =====
        List<Food> availableFoods = foodRepository.findAllWithRestaurant().stream()
                .filter(f -> f.getIsAvailable() != null && f.getIsAvailable())
                .collect(Collectors.toList());

        if (availableFoods.size() < MIN_ITEMS) {
            System.out.println("⚠️ FlashSaleRotation: Không đủ món ăn (" + availableFoods.size()
                    + "/" + MIN_ITEMS + ") để tạo Flashsale mới.");
            return null;
        }

        // ===== BƯỚC 3: Chọn ngẫu nhiên 3-5 món ăn =====
        Collections.shuffle(availableFoods);
        int itemCount = Math.min(
                ThreadLocalRandom.current().nextInt(MIN_ITEMS, MAX_ITEMS + 1),
                availableFoods.size()
        );
        List<Food> selectedFoods = availableFoods.subList(0, itemCount);

        // ===== BƯỚC 4: Tạo chiến dịch Flashsale mới (kéo dài 1 tiếng) =====
        LocalDateTime startTime = now;
        LocalDateTime endTime = now.plusHours(1);

        FlashSale flashSale = FlashSale.builder()
                .name("[AUTO] ⚡ Flash Sale " + startTime.format(HOUR_FORMAT) + " - " + endTime.format(HOUR_FORMAT))
                .startTime(startTime)
                .endTime(endTime)
                .isActive(true)
                .items(new ArrayList<>())
                .build();

        // ===== BƯỚC 5: Tạo các FlashSaleItem với mức giảm ngẫu nhiên 30%-50% =====
        for (Food food : selectedFoods) {
            double discountPercent = MIN_DISCOUNT_PERCENT
                    + ThreadLocalRandom.current().nextDouble() * (MAX_DISCOUNT_PERCENT - MIN_DISCOUNT_PERCENT);

            BigDecimal salePrice = food.getPrice()
                    .multiply(BigDecimal.valueOf(1 - discountPercent))
                    .setScale(0, RoundingMode.DOWN); // Làm tròn xuống (VD: 35,500 -> 35,000)

            // Đảm bảo giá sale không nhỏ hơn 1,000đ
            if (salePrice.compareTo(BigDecimal.valueOf(1000)) < 0) {
                salePrice = BigDecimal.valueOf(1000);
            }

            FlashSaleItem item = FlashSaleItem.builder()
                    .flashSale(flashSale)
                    .food(food)
                    .salePrice(salePrice)
                    .stockQuantity(DEFAULT_STOCK)
                    .soldQuantity(0)
                    .version(0)
                    .build();

            flashSale.getItems().add(item);
        }

        // ===== BƯỚC 6: Lưu vào database =====
        FlashSale saved = flashSaleRepository.save(flashSale);

        System.out.println("✅ FlashSaleRotation: Đã tạo Flashsale mới [" + saved.getName()
                + "] với " + saved.getItems().size() + " món:");
        for (FlashSaleItem item : saved.getItems()) {
            int discountPct = (int) ((1 - item.getSalePrice().doubleValue() / item.getFood().getPrice().doubleValue()) * 100);
            System.out.println("   🍜 " + item.getFood().getName()
                    + " | Giá gốc: " + item.getFood().getPrice() + "đ"
                    + " → Sale: " + item.getSalePrice() + "đ"
                    + " (-" + discountPct + "%)"
                    + " | Suất: " + item.getStockQuantity());
        }

        return saved;
    }
}
