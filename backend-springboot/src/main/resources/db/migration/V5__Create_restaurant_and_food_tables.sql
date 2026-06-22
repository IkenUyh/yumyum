 -- 1. Bảng Danh mục món ăn (Trà sữa, Cơm tấm, Đồ ăn vặt...)
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    image_url VARCHAR(255)
);

-- 2. Bảng Nhà hàng
CREATE TABLE restaurants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL, -- Chủ nhà hàng (liên kết với bảng users)
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    open_time TIME,
    close_time TIME,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_restaurant_merchant FOREIGN KEY (merchant_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Bảng Món ăn
CREATE TABLE foods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    category_id BIGINT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    image_url VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_food_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    CONSTRAINT fk_food_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);