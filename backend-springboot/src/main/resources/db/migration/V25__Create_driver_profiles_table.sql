-- Tạo hồ sơ riêng cho Tài xế để quản lý trạng thái nhận đơn
CREATE TABLE driver_profiles (
    user_id BIGINT PRIMARY KEY,
    vehicle_plate VARCHAR(20) NOT NULL, -- Biển số xe
    vehicle_type VARCHAR(50) DEFAULT 'Motorbike',
    status VARCHAR(20) DEFAULT 'OFFLINE', -- ONLINE (Đang chờ đơn), OFFLINE (Nghỉ), BUSY (Đang giao)
    current_order_id BIGINT, -- ID đơn hàng đang giao (nếu có)
    CONSTRAINT fk_dp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);