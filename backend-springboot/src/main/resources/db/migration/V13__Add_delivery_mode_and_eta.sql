-- Thêm trường Tốc độ giao hàng và Thời gian dự kiến đến nơi (ETA)
ALTER TABLE orders
    ADD COLUMN delivery_mode VARCHAR(20) DEFAULT 'STANDARD', -- STANDARD, FAST, EXPRESS
    ADD COLUMN expected_delivery_time TIMESTAMP;