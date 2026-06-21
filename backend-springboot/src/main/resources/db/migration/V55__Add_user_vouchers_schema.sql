-- V55__Add_user_vouchers_schema.sql
-- Thêm cột required_points vào bảng vouchers và tạo bảng user_vouchers

ALTER TABLE vouchers ADD COLUMN required_points INT DEFAULT 0;

CREATE TABLE user_vouchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    voucher_id BIGINT NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    acquired_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_uv_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_uv_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id) ON DELETE CASCADE
);
