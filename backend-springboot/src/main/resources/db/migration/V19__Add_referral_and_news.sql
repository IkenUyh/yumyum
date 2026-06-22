-- 1. Cập nhật bảng users để hỗ trợ tính năng Mời bạn bè
ALTER TABLE users
    ADD COLUMN referral_code VARCHAR(20) UNIQUE, -- Mã giới thiệu của bản thân
    ADD COLUMN referred_by_id BIGINT;            -- ID của người đã giới thiệu mình

-- Sinh mã giới thiệu mặc định cho các user cũ để không bị lỗi
UPDATE users SET referral_code = CONCAT('FD', id, FLOOR(RAND() * 1000)) WHERE referral_code IS NULL;

-- 2. Tạo bảng Tin tức & Khuyến mãi
CREATE TABLE news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);