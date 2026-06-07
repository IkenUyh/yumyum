-- Cập nhật tọa độ cho Nhà hàng để tính phí Ship
ALTER TABLE restaurants
    ADD COLUMN latitude DECIMAL(10, 8),
    ADD COLUMN longitude DECIMAL(11, 8);

-- Update data giả lập cho mấy quán cũ có tọa độ (Ví dụ tọa độ khu vực Landmark 81)
UPDATE restaurants SET latitude = 10.7952, longitude = 106.7218 WHERE id > 0;