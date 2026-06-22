package com.uit.fooddelivery_api.common.utils;

import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.entities.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class OrderRevenueUtil {
    public static BigDecimal calculateOriginalRevenue(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        if (order != null && order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                BigDecimal originalFoodPrice = BigDecimal.ZERO;
                if (item.getFood() != null && item.getFood().getPrice() != null) {
                    originalFoodPrice = item.getFood().getPrice();
                }
                
                BigDecimal optionsPrice = BigDecimal.ZERO;
                if (item.getSelectedOptions() != null && !item.getSelectedOptions().isEmpty()
                        && !item.getSelectedOptions().equals("[]")) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<Map<String, Object>> parsedOpts = mapper.readValue(item.getSelectedOptions(),
                                new TypeReference<List<Map<String, Object>>>() {
                                });
                        for (Map<String, Object> opt : parsedOpts) {
                            if (opt.get("price") != null) {
                                optionsPrice = optionsPrice.add(new BigDecimal(opt.get("price").toString()));
                            }
                        }
                    } catch (Exception e) {
                        // ignore parsing errors
                    }
                }
                
                BigDecimal originalItemPrice = originalFoodPrice.add(optionsPrice);
                int qty = item.getQuantity() != null ? item.getQuantity() : 0;
                total = total.add(originalItemPrice.multiply(BigDecimal.valueOf(qty)));
            }
        }
        return total;
    }
}
