-- Bảng danh sách yêu thích nhà hàng của người dùng
CREATE TABLE favorite_restaurants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    -- Mỗi user chỉ được thêm 1 nhà hàng vào yêu thích 1 lần
    CONSTRAINT uq_user_restaurant UNIQUE (user_id, restaurant_id)
);
