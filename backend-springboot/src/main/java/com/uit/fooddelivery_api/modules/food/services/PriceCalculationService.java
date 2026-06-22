package com.uit.fooddelivery_api.modules.food.services;

import com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem;
import com.uit.fooddelivery_api.modules.flashsale.repositories.FlashSaleItemRepository;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceCalculationService {

    private final FlashSaleItemRepository flashSaleItemRepository;

    public static class PriceResult {
        public BigDecimal finalPrice;
        public BigDecimal originalPrice;
        public String discountType;

        public PriceResult(BigDecimal finalPrice, BigDecimal originalPrice, String discountType) {
            this.finalPrice = finalPrice;
            this.originalPrice = originalPrice;
            this.discountType = discountType;
        }
    }

    public PriceResult calculateFinalPrice(Food food, String requestedPromotion) {
        BigDecimal originalPrice = food.getPrice();
        if (originalPrice == null) {
            return new PriceResult(BigDecimal.ZERO, BigDecimal.ZERO, null);
        }

        // Nếu không có yêu cầu khuyến mãi đặc biệt nào từ giao diện (hoặc nguồn NORMAL)
        if (requestedPromotion == null || "NORMAL".equalsIgnoreCase(requestedPromotion) || requestedPromotion.isEmpty()) {
            return new PriceResult(originalPrice, originalPrice, null);
        }

        if ("FLASHSALE".equalsIgnoreCase(requestedPromotion)) {
            java.util.Optional<FlashSaleItem> flashSaleOpt = flashSaleItemRepository.findActiveFlashSaleItemByFoodId(food.getId(), LocalDateTime.now());
            if (flashSaleOpt.isPresent()) {
                return new PriceResult(flashSaleOpt.get().getSalePrice(), originalPrice, "FLASHSALE");
            }
        }

        if ("DEAL".equalsIgnoreCase(requestedPromotion) || "DEAL 20%".equalsIgnoreCase(requestedPromotion)) {
            BigDecimal dealPrice = originalPrice.multiply(BigDecimal.valueOf(0.8));
            return new PriceResult(dealPrice, originalPrice, "DEAL 20%");
        }

        // Fallback về giá gốc nếu promotion request không hợp lệ hoặc đã hết hạn
        return new PriceResult(originalPrice, originalPrice, null);
    }
    
    public PriceResult calculateFinalPrice(Food food) {
        return calculateFinalPrice(food, null);
    }

    public Map<Long, PriceResult> calculateFinalPrices(List<Food> foods) {
        return calculateFinalPrices(foods, null);
    }

    public Map<Long, PriceResult> calculateFinalPrices(List<Food> foods, String requestedPromotion) {
        List<FlashSaleItem> activeFS = flashSaleItemRepository.findActiveFlashSaleItems(LocalDateTime.now());
        Map<Long, FlashSaleItem> fsMap = activeFS.stream()
                .collect(Collectors.toMap(fs -> fs.getFood().getId(), fs -> fs, (f1, f2) -> f1));

        Map<Long, PriceResult> results = new HashMap<>();
        for (Food food : foods) {
            BigDecimal originalPrice = food.getPrice();
            if (originalPrice == null) {
                results.put(food.getId(), new PriceResult(BigDecimal.ZERO, BigDecimal.ZERO, null));
                continue;
            }
            if ("FLASHSALE".equalsIgnoreCase(requestedPromotion)) {
                FlashSaleItem fsItem = fsMap.get(food.getId());
                if (fsItem != null) {
                    results.put(food.getId(), new PriceResult(fsItem.getSalePrice(), originalPrice, "FLASHSALE"));
                    continue;
                }
            } else if ("DEAL".equalsIgnoreCase(requestedPromotion) || "DEAL 20%".equalsIgnoreCase(requestedPromotion)) {
                BigDecimal dealPrice = originalPrice.multiply(BigDecimal.valueOf(0.8));
                results.put(food.getId(), new PriceResult(dealPrice, originalPrice, "DEAL 20%"));
                continue;
            }
            results.put(food.getId(), new PriceResult(originalPrice, originalPrice, null));
        }
        return results;
    }
}
