-- V29__Add_System_Params_And_Batching.sql
-- Issue #36: Bảng cấu hình tham số hệ thống động
CREATE TABLE system_parameters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    param_key VARCHAR(100) UNIQUE NOT NULL,
    param_value VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

-- Insert sẵn các tham số mặc định
INSERT INTO system_parameters (param_key, param_value, description) VALUES
    ('LOYALTY_BASE_POINTS', '100', 'Điểm danh cơ bản'),
    ('LOYALTY_STREAK_BONUS', '50', 'Thưởng chuỗi điểm danh'),
    ('DRIVER_MIN_DEPOSIT', '50000', 'Số dư ví tối thiểu để tài xế nhận đơn'),
    ('DRIVER_MAX_BATCH_ORDERS', '2', 'Số đơn tối đa tài xế được phép ghép');

-- Issue #25: Thêm cột đếm số đơn đang giao đồng thời của tài xế để phục vụ Batching
ALTER TABLE driver_profiles
    ADD COLUMN current_order_count INT DEFAULT 0;