-- V33__Add_dummy_orders.sql
-- Thêm dữ liệu giả cho đơn hàng (đang đến và đã giao) của các khách hàng mẫu

-- 1. Tạo tài xế mẫu nếu chưa tồn tại để gán cho các đơn hàng vận chuyển
INSERT INTO users (phone_number, password, full_name, role, is_active, email)
VALUES ('0999888777', '123456', 'Nguyễn Văn Tài Xế', 'DRIVER', TRUE, 'driver@fooddelivery.com');
SET @driver_id = LAST_INSERT_ID();

INSERT INTO driver_profiles (user_id, vehicle_plate, vehicle_type, status)
VALUES (@driver_id, '59-X3 678.90', 'Motorbike', 'BUSY');

-- 2. Lấy ID người dùng mẫu
SET @user_huy = (SELECT id FROM users WHERE phone_number = '0987301126');
SET @user_dat = (SELECT id FROM users WHERE phone_number = '0376171242');
SET @user_pbqhuy = (SELECT id FROM users WHERE phone_number = '0329815572');
SET @user_dan = (SELECT id FROM users WHERE phone_number = '6767676767');

-- 3. Lấy ID nhà hàng mẫu
SET @res_com = (SELECT id FROM restaurants WHERE name = 'Cơm Tấm Phúc Lộc Thọ - Đinh Tiên Hoàng');
SET @res_banhmi = (SELECT id FROM restaurants WHERE name = 'Bánh Mì Huỳnh Hoa');
SET @res_sushi = (SELECT id FROM restaurants WHERE name = 'Sushi Hokkaido Sachi - Đồng Khởi');

-- 4. Lấy ID món ăn mẫu tương ứng
SET @food_surn = (SELECT id FROM foods WHERE name = 'Cơm Sườn Nướng Mật Ong' AND restaurant_id = @res_com);
SET @food_surn_bi = (SELECT id FROM foods WHERE name = 'Cơm Sườn Bì Chả' AND restaurant_id = @res_com);
SET @food_canh = (SELECT id FROM foods WHERE name = 'Canh Khổ Qua Nhồi Thịt' AND restaurant_id = @res_com);

SET @food_bm_db = (SELECT id FROM foods WHERE name = 'Bánh Mì Đặc Biệt' AND restaurant_id = @res_banhmi);

SET @food_sashimi = (SELECT id FROM foods WHERE name = 'Sashimi Cá Hồi Tươi' AND restaurant_id = @res_sushi);
SET @food_sushi_combo = (SELECT id FROM foods WHERE name = 'Combo Sushi Mẫu' AND restaurant_id = @res_sushi);

-- =============================================================================
-- ĐƠN HÀNG ĐANG ĐẾN (dang_den: PENDING / PREPARING / DELIVERING)
-- =============================================================================

-- Đơn hàng 1: Đang giao (DELIVERING) - Khách hàng: Cao Đạt
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, pickup_code, delivery_pin, created_at)
VALUES (@user_dat, @res_com, 114000.00, 'DELIVERING', @driver_id, 15000.00, 0.00, 'STANDARD', DATE_ADD(NOW(), INTERVAL 30 MINUTE), 'PICK123', 'PIN456', NOW());
SET @order1_id = LAST_INSERT_ID();

INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order1_id, @food_surn, 2, 42000.00);
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order1_id, @food_canh, 1, 15000.00);

-- Đơn hàng 2: Đang chuẩn bị (PREPARING) - Khách hàng: Huy Onii-chan
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, pickup_code, delivery_pin, created_at)
VALUES (@user_huy, @res_banhmi, 80000.00, 'PREPARING', NULL, 15000.00, 0.00, 'STANDARD', DATE_ADD(NOW(), INTERVAL 45 MINUTE), 'PICK999', 'PIN888', NOW());
SET @order2_id = LAST_INSERT_ID();

INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order2_id, @food_bm_db, 1, 65000.00);

-- =============================================================================
-- ĐƠN HÀNG ĐÃ GIAO (da_giao: COMPLETED)
-- =============================================================================

-- Đơn hàng 3: Đã hoàn thành - Khách hàng: Cao Đạt (Hôm qua)
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, created_at)
VALUES (@user_dat, @res_com, 70000.00, 'COMPLETED', @driver_id, 15000.00, 0.00, 'STANDARD', DATE_SUB(NOW(), INTERVAL 23 HOUR), DATE_SUB(NOW(), INTERVAL 24 HOUR));
SET @order3_id = LAST_INSERT_ID();

INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order3_id, @food_surn_bi, 1, 55000.00);

-- Đơn hàng 4: Đã hoàn thành - Khách hàng: Huy Onii-chan (2 ngày trước)
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, created_at)
VALUES (@user_huy, @res_banhmi, 145000.00, 'COMPLETED', @driver_id, 15000.00, 0.00, 'STANDARD', DATE_SUB(NOW(), INTERVAL 47 HOUR), DATE_SUB(NOW(), INTERVAL 48 HOUR));
SET @order4_id = LAST_INSERT_ID();

INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order4_id, @food_bm_db, 2, 65000.00);

-- Đơn hàng 5: Đã hoàn thành - Khách hàng: pbqhuy (3 ngày trước)
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, created_at)
VALUES (@user_pbqhuy, @res_sushi, 400000.00, 'COMPLETED', @driver_id, 15000.00, 0.00, 'STANDARD', DATE_SUB(NOW(), INTERVAL 71 HOUR), DATE_SUB(NOW(), INTERVAL 72 HOUR));
SET @order5_id = LAST_INSERT_ID();

INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order5_id, @food_sashimi, 1, 165000.00);
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order5_id, @food_sushi_combo, 1, 220000.00);

-- Đơn hàng 6: Đã hoàn thành - Khách hàng: Trần Thanh Dân (4 ngày trước)
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, created_at)
VALUES (@user_dan, @res_com, 57000.00, 'COMPLETED', @driver_id, 15000.00, 0.00, 'STANDARD', DATE_SUB(NOW(), INTERVAL 95 HOUR), DATE_SUB(NOW(), INTERVAL 96 HOUR));
SET @order6_id = LAST_INSERT_ID();

INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order6_id, @food_surn, 1, 42000.00);
