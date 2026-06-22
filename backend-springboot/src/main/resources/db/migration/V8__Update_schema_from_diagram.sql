-- 1. Bảng SOTAY_DIACHI -> user_addresses (Lưu địa chỉ để tính khoảng cách Issue #8)
CREATE TABLE user_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_name VARCHAR(100) NOT NULL, -- Tên gọi (VD: Nhà riêng, Công ty)
    recipient_name VARCHAR(255) NOT NULL, -- Tên người nhận
    phone_number VARCHAR(15) NOT NULL, -- Số điện thoại nhận
    detailed_address VARCHAR(255) NOT NULL, -- Địa chỉ chi tiết
    latitude DECIMAL(10, 8), -- Vĩ độ
    longitude DECIMAL(11, 8), -- Kinh độ
    is_default BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. Bảng GIOHANG -> cart_items
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_food FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE
);

-- 3. Bảng VOUCHER -> vouchers (Epic 2)
CREATE TABLE vouchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- Tên Voucher / Mã Voucher
    type VARCHAR(20) NOT NULL, -- SHIPPING_DISCOUNT, ORDER_DISCOUNT
    discount_percent INT, -- Phần trăm giảm
    max_discount DECIMAL(19, 2), -- Giảm tối đa
    min_order_value DECIMAL(19, 2), -- Đơn tối thiểu
    stock_quantity INT NOT NULL, -- Số lượng voucher
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- 4. Bảng THANHTOAN -> payments (Epic 3 - Issue #22)
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- CASH, ZALOPAY, MOMO, WALLET
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED
    transaction_id VARCHAR(100), -- Mã giao dịch từ ví điện tử
    amount DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    -- Sẽ add foreign key sau khi tạo bảng
);

-- 5. Bảng DANHGIA -> reviews (Epic 3 - Issue #21)
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Bảng TICHXU -> loyalty_points (Epic 2 - Issue #18)
CREATE TABLE loyalty_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    current_points INT DEFAULT 0,
    checkin_streak INT DEFAULT 0,
    CONSTRAINT fk_loyalty_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. CẬP NHẬT BẢNG DONHANG (orders) THEO SƠ ĐỒ MỚI
ALTER TABLE orders
    ADD COLUMN address_id BIGINT,
    ADD COLUMN voucher_id BIGINT,
    ADD COLUMN shipping_fee DECIMAL(19, 2) DEFAULT 0.00,
    ADD COLUMN discount_amount DECIMAL(19, 2) DEFAULT 0.00,
    ADD COLUMN shipping_code VARCHAR(100); -- MaVanDon

-- Thêm Foreign Keys cho bảng orders và các bảng liên quan
ALTER TABLE orders ADD CONSTRAINT fk_order_address FOREIGN KEY (address_id) REFERENCES user_addresses(id) ON DELETE SET NULL;
ALTER TABLE orders ADD CONSTRAINT fk_order_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id) ON DELETE SET NULL;
ALTER TABLE payments ADD CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;
ALTER TABLE reviews ADD CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;
ALTER TABLE reviews ADD CONSTRAINT fk_review_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE;