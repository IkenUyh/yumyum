-- 1. Bảng Đơn hàng (Tổng quan)
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,          -- Khách hàng đặt đơn
    restaurant_id BIGINT NOT NULL,     -- Đặt ở nhà hàng nào
    total_amount DECIMAL(19, 2) NOT NULL, -- Tổng tiền của đơn hàng
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, PREPARING, DELIVERING, COMPLETED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_order_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- 2. Bảng Chi tiết đơn hàng (Mua những món gì, số lượng bao nhiêu)
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(19, 2) NOT NULL, -- Lưu giá lúc mua để tránh sau này quán đổi giá món ăn làm sai lệch lịch sử
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_food FOREIGN KEY (food_id) REFERENCES foods(id)
);