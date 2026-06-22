package com.uit.fooddelivery_api.modules.grouporder.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.grouporder.entities.GroupOrder;
import com.uit.fooddelivery_api.modules.grouporder.entities.GroupOrderItem;
import com.uit.fooddelivery_api.modules.grouporder.repositories.GroupOrderItemRepository;
import com.uit.fooddelivery_api.modules.grouporder.repositories.GroupOrderRepository;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupOrderService {

    private final GroupOrderRepository groupOrderRepository;
    private final GroupOrderItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;

    // 1. Nhóm trưởng tạo phòng
    @Transactional
    public GroupOrder createRoom(Long restaurantId, User host) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quán!"));

        // Sinh mã code 6 ký tự ngẫu nhiên (VD: K8B9X1)
        String roomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        GroupOrder groupOrder = GroupOrder.builder()
                .host(host)
                .restaurant(restaurant)
                .roomCode(roomCode)
                .status("OPEN")
                .build();

        return groupOrderRepository.save(groupOrder);
    }

    // 2. Thành viên chọn món và thêm vào phòng chung
    @Transactional
    public void addItemToRoom(String roomCode, Long foodId, Integer quantity, String optionsJson, User member) {
        GroupOrder room = groupOrderRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại!"));

        if (!room.getStatus().equals("OPEN")) {
            throw new RuntimeException("Phòng này đã bị khóa (Nhóm trưởng đã chốt đơn)!");
        }

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món!"));

        if (!food.getRestaurant().getId().equals(room.getRestaurant().getId())) {
            throw new RuntimeException("Món này không thuộc quán mà nhóm đang đặt!");
        }

        GroupOrderItem item = GroupOrderItem.builder()
                .groupOrder(room)
                .user(member)
                .food(food)
                .quantity(quantity)
                .selectedOptions(optionsJson != null ? optionsJson : "[]")
                .build();

        itemRepository.save(item);
    }

    // 3. THUẬT TOÁN SPLIT BILL: Nhóm trưởng chốt đơn và chia tiền
    @Transactional
    public Map<String, Object> closeRoomAndSplitBill(String roomCode, User host, BigDecimal totalShippingFee) {
        GroupOrder room = groupOrderRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại!"));

        if (!room.getHost().getId().equals(host.getId())) {
            throw new RuntimeException("Chỉ có Nhóm trưởng mới được quyền chốt đơn!");
        }

        room.setStatus("LOCKED");
        groupOrderRepository.save(room);

        // Map lưu tổng tiền từng người phải trả: Key = Tên người dùng, Value = Số tiền
        Map<String, BigDecimal> userTotals = new HashMap<>();
        Set<Long> uniqueUsers = new HashSet<>();
        ObjectMapper mapper = new ObjectMapper();
        BigDecimal totalFoodCostOfRoom = BigDecimal.ZERO;

        for (GroupOrderItem item : room.getItems()) {
            uniqueUsers.add(item.getUser().getId());
            String userName = item.getUser().getFullName();

            // Tính tiền món gốc
            BigDecimal itemTotal = item.getFood().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            // Tính thêm tiền Topping (đọc từ JSON)
            if (item.getSelectedOptions() != null && !item.getSelectedOptions().equals("[]")) {
                try {
                    List<Map<String, Object>> parsedOpts = mapper.readValue(item.getSelectedOptions(), new TypeReference<List<Map<String, Object>>>() {});
                    BigDecimal optionsPrice = BigDecimal.ZERO;
                    for (Map<String, Object> opt : parsedOpts) {
                        optionsPrice = optionsPrice.add(new BigDecimal(opt.get("price").toString()));
                    }
                    itemTotal = itemTotal.add(optionsPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi phân tích dữ liệu Topping!");
                }
            }

            // Cộng dồn vào ví của người đó trong Map
            userTotals.put(userName, userTotals.getOrDefault(userName, BigDecimal.ZERO).add(itemTotal));
            totalFoodCostOfRoom = totalFoodCostOfRoom.add(itemTotal);
        }

        // Tính tiền ship chia đều cho mỗi người
        int memberCount = uniqueUsers.size();
        BigDecimal shippingPerPerson = BigDecimal.ZERO;
        if (memberCount > 0 && totalShippingFee != null) {
            shippingPerPerson = totalShippingFee.divide(BigDecimal.valueOf(memberCount), 0, RoundingMode.HALF_UP);
        }

        // Cộng tiền ship vào hóa đơn của từng người
        List<Map<String, Object>> finalBillBreakdown = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : userTotals.entrySet()) {
            Map<String, Object> personBill = new HashMap<>();
            personBill.put("memberName", entry.getKey());
            personBill.put("foodTotal", entry.getValue());
            personBill.put("sharedShipping", shippingPerPerson);
            personBill.put("finalToPay", entry.getValue().add(shippingPerPerson)); // Tổng tiền người này phải CK cho host
            finalBillBreakdown.add(personBill);
        }

        // Đóng gói trả về Frontend
        Map<String, Object> response = new HashMap<>();
        response.put("roomCode", roomCode);
        response.put("totalFoodCost", totalFoodCostOfRoom);
        response.put("totalShippingCost", totalShippingFee);
        response.put("grandTotal", totalFoodCostOfRoom.add(totalShippingFee != null ? totalShippingFee : BigDecimal.ZERO));
        response.put("splitBillDetails", finalBillBreakdown);

        return response;
    }
}