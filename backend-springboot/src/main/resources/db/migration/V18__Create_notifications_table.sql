-- Bảng lưu trữ thông báo để khách có thể xem lại lịch sử
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL, -- Thông báo gửi cho ai
    title VARCHAR(255) NOT NULL, -- Tiêu đề (VD: Đơn hàng đang được giao)
    message TEXT NOT NULL, -- Nội dung chi tiết
    type VARCHAR(50) NOT NULL, -- ORDER_UPDATE, SYSTEM, PROMOTION
    is_read BOOLEAN DEFAULT FALSE, -- Trạng thái chưa đọc / đã đọc
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_noti_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);