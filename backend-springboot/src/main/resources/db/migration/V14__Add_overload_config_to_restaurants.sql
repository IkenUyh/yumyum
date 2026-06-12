-- Thêm Cầu dao thủ công và Giới hạn đơn hàng để chống quá tải
ALTER TABLE restaurants
    ADD COLUMN is_accepting_orders BOOLEAN DEFAULT TRUE, -- Cầu dao đóng/mở quán khẩn cấp
    ADD COLUMN max_pending_orders INT DEFAULT 20;        -- Số đơn tối đa chịu được cùng lúc