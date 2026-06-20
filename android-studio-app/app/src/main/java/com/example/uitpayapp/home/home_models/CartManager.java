package com.example.uitpayapp.home.home_models;

import com.example.uitpayapp.modules.cart.CartRepository;
import com.example.uitpayapp.modules.cart.models.requests.CartItemRequestDTO;
import com.example.uitpayapp.modules.cart.models.responses.CartItemResponseDTO;
import com.example.uitpayapp.network.ApiCallback;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class CartManager {

    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();
    private final List<CartItem> lastOrder = new ArrayList<>();
    private final CartRepository cartRepository = new CartRepository();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartItem> getCart() {
        return cartItems;
    }

    // Server Synchronization Helpers

    public static Long getDbFoodId(String clientFoodId) {
        if (clientFoodId == null) return 1L;
        String digits = clientFoodId.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            long id = Math.abs((long) clientFoodId.hashCode()) % 150;
            return id == 0 ? 1L : id;
        }
        try {
            long id = Long.parseLong(digits);
            if (id <= 0) return 1L;
            return id;
        } catch (NumberFormatException e) {
            return 1L;
        }
    }

    public static CartItem mapResponseToCartItem(CartItemResponseDTO dto) {
        int imageResId = 0;
        if (dto.getFoodName() != null) {
            String lower = dto.getFoodName().toLowerCase();
            if (lower.contains("trà") || lower.contains("sữa") || lower.contains("phandi") || lower.contains("freeze")) {
                imageResId = 0;
            } else if (lower.contains("cà phê") || lower.contains("phin")) {
                imageResId = 0;
            } else if (lower.contains("pizza")) {
                imageResId = 0;
            }
        }

        FoodMenuItem menuItem = new FoodMenuItem(
            String.valueOf(dto.getFoodId()),
            dto.getFoodName(),
            dto.getBasePrice() != null ? dto.getBasePrice().longValue() : 0L,
            imageResId,
            dto.getFoodName() != null ? dto.getFoodName() : "",
            dto.getFoodImageUrl()
        );

        List<CartTopping> toppings = new ArrayList<>();
        if (dto.getSelectedOptions() != null) {
            for (java.util.Map<String, Object> opt : dto.getSelectedOptions()) {
                String toppingName = opt.containsKey("name") && opt.get("name") != null ? opt.get("name").toString() : "";
                long toppingPrice = 0;
                if (opt.containsKey("price") && opt.get("price") != null) {
                    toppingPrice = ((Number) opt.get("price")).longValue();
                }
                toppings.add(new CartTopping(toppingName, toppingName, toppingPrice));
            }
        }

        return new CartItem(
            dto.getId(),
            menuItem,
            dto.getQuantity() != null ? dto.getQuantity() : 0,
            toppings
        );
    }

    public void fetchCartFromServer(final ApiCallback<List<CartItem>> callback) {
        cartRepository.getCart(new ApiCallback<List<CartItemResponseDTO>>() {
            @Override
            public void onSuccess(List<CartItemResponseDTO> data) {
                cartItems.clear();
                if (data != null) {
                    for (CartItemResponseDTO dto : data) {
                        cartItems.add(mapResponseToCartItem(dto));
                    }
                }
                callback.onSuccess(cartItems);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    private List<Long> getToppingIds(CartItem item) {
        List<Long> toppingIds = new ArrayList<>();
        if (item.getSelectedToppings() != null) {
            for (CartTopping topping : item.getSelectedToppings()) {
                String tid = topping.getId();
                if (tid != null) {
                    if (tid.startsWith("opt_")) {
                        try {
                            toppingIds.add(Long.parseLong(tid.substring(4)));
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    } else {
                        try {
                            toppingIds.add(Long.parseLong(tid));
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    }
                }
            }
        }
        return toppingIds;
    }

    public void addItemSync(CartItem item, final ApiCallback<String> callback) {
        Long foodId = getDbFoodId(item.getMenuItem().getId());
        List<Long> toppingIds = getToppingIds(item);
        CartItemRequestDTO dto = new CartItemRequestDTO(foodId, item.getQuantity(), toppingIds);
        cartRepository.addOrUpdateItem(dto, new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                fetchCartFromServer(new ApiCallback<List<CartItem>>() {
                    @Override
                    public void onSuccess(List<CartItem> items) {
                        callback.onSuccess(data);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onSuccess(data); // Call success anyway since item was added
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void removeItemSync(int position, final ApiCallback<String> callback) {
        if (position < 0 || position >= cartItems.size()) {
            callback.onError("Vị trí không hợp lệ.");
            return;
        }
        CartItem item = cartItems.get(position);
        if (item.getDbId() == null) {
            cartItems.remove(position);
            callback.onSuccess("Đã xóa.");
            return;
        }
        cartRepository.removeItem(item.getDbId(), new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                fetchCartFromServer(new ApiCallback<List<CartItem>>() {
                    @Override
                    public void onSuccess(List<CartItem> items) {
                        callback.onSuccess(data);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onSuccess(data);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void updateQuantitySync(int position, int quantity, final ApiCallback<String> callback) {
        if (position < 0 || position >= cartItems.size()) {
            callback.onError("Vị trí không hợp lệ.");
            return;
        }
        CartItem item = cartItems.get(position);
        int currentQty = item.getQuantity();
        int diff = quantity - currentQty;
        if (diff == 0) {
            callback.onSuccess("Không có thay đổi.");
            return;
        }
        Long foodId = getDbFoodId(item.getMenuItem().getId());
        List<Long> toppingIds = getToppingIds(item);
        CartItemRequestDTO dto = new CartItemRequestDTO(foodId, diff, toppingIds);
        cartRepository.addOrUpdateItem(dto, new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                fetchCartFromServer(new ApiCallback<List<CartItem>>() {
                    @Override
                    public void onSuccess(List<CartItem> items) {
                        callback.onSuccess(data);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onSuccess(data);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void updateItemSync(int position, CartItem newItem, final ApiCallback<String> callback) {
        if (position < 0 || position >= cartItems.size()) {
            callback.onError("Vị trí không hợp lệ.");
            return;
        }
        CartItem oldItem = cartItems.get(position);
        boolean toppingsChanged = !new HashSet<>(oldItem.getSelectedToppings()).equals(new HashSet<>(newItem.getSelectedToppings()));
        
        if (!toppingsChanged) {
            updateQuantitySync(position, newItem.getQuantity(), callback);
        } else {
            if (oldItem.getDbId() == null) {
                addItemSync(newItem, callback);
            } else {
                cartRepository.removeItem(oldItem.getDbId(), new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        addItemSync(newItem, callback);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        addItemSync(newItem, callback);
                    }
                });
            }
        }
    }

    public void clearCartSync(final ApiCallback<String> callback) {
        cartRepository.clearCart(new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                cartItems.clear();
                callback.onSuccess(data);
            }

            @Override
            public void onError(String errorMessage) {
                cartItems.clear(); // Clear locally anyway to preserve user experience
                callback.onSuccess("Giỏ hàng cục bộ đã được xóa.");
            }
        });
    }

    public void getCartCountSync(final ApiCallback<Integer> callback) {
        cartRepository.getCartCount(callback);
    }

    // Local compatibility methods

    public void addItem(CartItem item) {
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem existing = cartItems.get(i);
            boolean sameName = existing.getMenuItem().getName().equals(item.getMenuItem().getName());
            boolean sameToppings = new HashSet<>(existing.getSelectedToppings()).equals(new HashSet<>(item.getSelectedToppings()));
            
            if (sameName && sameToppings) {
                int newQty = existing.getQuantity() + item.getQuantity();
                CartItem updatedItem = new CartItem(item.getDbId(), item.getMenuItem(), newQty, item.getSelectedToppings());
                cartItems.set(i, updatedItem);
                return;
            }
        }
        cartItems.add(item);
    }
    
    public void updateItem(int position, CartItem newItem) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            
            boolean merged = false;
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem existing = cartItems.get(i);
                boolean sameName = existing.getMenuItem().getName().equals(newItem.getMenuItem().getName());
                boolean sameToppings = new HashSet<>(existing.getSelectedToppings()).equals(new HashSet<>(newItem.getSelectedToppings()));
                
                if (sameName && sameToppings) {
                    int newQty = existing.getQuantity() + newItem.getQuantity();
                    CartItem updatedItem = new CartItem(newItem.getDbId(), newItem.getMenuItem(), newQty, newItem.getSelectedToppings());
                    cartItems.set(i, updatedItem);
                    merged = true;
                    break;
                }
            }
            
            if (!merged) {
                cartItems.add(position, newItem);
            }
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void updateQuantity(int position, int quantity) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.get(position).setQuantity(quantity);
        }
    }

    public void clearCart() {
        lastOrder.clear();
        lastOrder.addAll(cartItems);
        cartItems.clear();
    }

    public List<CartItem> getLastOrder() {
        return lastOrder;
    }

    public long getTotalPrice() {
        long total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getTotalItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public String getFormattedTotalPrice() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(getTotalPrice()) + "đ";
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public String getProductSummary() {
        StringBuilder sb = new StringBuilder();
        for (CartItem ci : cartItems) {
            if (sb.length() > 0) {
                sb.append("<br>");
            }
            sb.append("<b>").append(ci.getQuantity()).append("x ").append(ci.getMenuItem().getName()).append("</b>");
            String toppings = ci.getToppingsString();
            if (toppings != null && !toppings.isEmpty()) {
                sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;+ ").append(toppings);
            }
        }
        return sb.toString();
    }
}
