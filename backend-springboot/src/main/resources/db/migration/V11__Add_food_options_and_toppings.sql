-- 1. Bảng Nhóm tùy chọn (VD: "Kích cỡ", "Lượng đường", "Topping thêm")
CREATE TABLE food_option_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_required BOOLEAN DEFAULT FALSE, -- Bắt buộc chọn (VD: Size) hay Không bắt buộc (VD: Topping)
    max_choices INT DEFAULT 1, -- Số lượng tối đa được chọn trong nhóm này (Topping thì có thể > 1)
    CONSTRAINT fk_group_food FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE
);

-- 2. Bảng Chi tiết tùy chọn (VD: "Size L (+10k)", "50% Đường (+0k)", "Trân châu (+5k)")
CREATE TABLE food_option_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    additional_price DECIMAL(19, 2) DEFAULT 0.00, -- Tiền cộng thêm
    is_available BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_item_group FOREIGN KEY (group_id) REFERENCES food_option_groups(id) ON DELETE CASCADE
);

-- 3. Lưu vết Topping vào Giỏ Hàng và Đơn Hàng dưới dạng JSON String (Cách tối ưu nhất của E-commerce)
ALTER TABLE cart_items ADD COLUMN selected_options TEXT;
ALTER TABLE order_items ADD COLUMN selected_options TEXT;