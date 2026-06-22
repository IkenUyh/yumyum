-- Thêm mã xác thực lấy hàng và mã PIN nhận hàng để chống gian lận trong khâu vận hành
ALTER TABLE orders
    ADD COLUMN pickup_code VARCHAR(10),
    ADD COLUMN delivery_pin VARCHAR(10);