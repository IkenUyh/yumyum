-- Bảng lưu trữ FCM tokens của người dùng
CREATE TABLE user_fcm_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    fcm_token VARCHAR(255) NOT NULL UNIQUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_fcm_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
