-- Thêm 2 cột để lưu trữ sẵn điểm đánh giá, tránh việc phải JOIN với bảng reviews liên tục làm chậm hệ thống
ALTER TABLE restaurants
    ADD COLUMN rating_average DECIMAL(3, 1) DEFAULT 0.0,
    ADD COLUMN review_count INT DEFAULT 0;

-- Lưu ý: Sau này mỗi khi có khách hàng viết Review mới,
-- bạn chỉ cần cộng dồn và tính lại trung bình rồi Update thẳng vào 2 cột này.