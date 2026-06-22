-- 1. Bảng Phòng Đặt Nhóm
CREATE TABLE group_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id BIGINT NOT NULL, -- Trưởng phòng
    restaurant_id BIGINT NOT NULL, -- Quán đang đặt
    room_code VARCHAR(10) UNIQUE NOT NULL, -- Mã phòng (VD: UIT888)
    status VARCHAR(30) DEFAULT 'OPEN', -- OPEN (Đang chọn món), LOCKED (Đã chốt)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_go_host FOREIGN KEY (host_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_go_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- 2. Bảng Giỏ Hàng Nhóm (Lưu món ăn của từng người trong phòng)
CREATE TABLE group_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL, -- Người chọn món này
    food_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    selected_options TEXT, -- Topping giống bảng giỏ hàng cá nhân
    CONSTRAINT fk_goi_group FOREIGN KEY (group_order_id) REFERENCES group_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_goi_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_goi_food FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE
);