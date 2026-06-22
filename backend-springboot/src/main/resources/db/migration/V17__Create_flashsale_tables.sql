-- 1. Bảng Chiến dịch Flashsale (VD: Ngày đôi 6/6)
CREATE TABLE flash_sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- 2. Bảng Chi tiết món ăn trong Flashsale
CREATE TABLE flash_sale_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flash_sale_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    sale_price DECIMAL(19, 2) NOT NULL, -- Giá sau khi giảm
    stock_quantity INT NOT NULL,        -- Tổng suất bán (VD: 50 ly)
    sold_quantity INT DEFAULT 0,        -- Đã bán được bao nhiêu
    version INT DEFAULT 0,              -- Optimistic Locking
    CONSTRAINT fk_fsi_sale FOREIGN KEY (flash_sale_id) REFERENCES flash_sales(id) ON DELETE CASCADE,
    CONSTRAINT fk_fsi_food FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE
);