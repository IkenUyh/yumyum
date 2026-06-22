CREATE TABLE merchant_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_name VARCHAR(255) NOT NULL,
    store_address VARCHAR(255) NOT NULL,
    store_phone VARCHAR(20) NOT NULL,
    business_license_url VARCHAR(255), -- Link ảnh Giấy Phép Kinh Doanh (Tải lên Cloudinary)
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    confirmation_code VARCHAR(20), -- Mã xác nhận
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_request_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);