-- V32__Add_bbq_and_haisan_categories.sql

-- =============================================================================
-- 1. THÊM CÁC DANH MỤC MỚI (BBQ & HẢI SẢN)
-- =============================================================================
INSERT INTO categories (name, image_url) VALUES ('Đồ Nướng & BBQ', 'https://res.cloudinary.com/demo/bbq.jpg');
SET @cat_bbq = LAST_INSERT_ID();

INSERT INTO categories (name, image_url) VALUES ('Hải Sản', 'https://res.cloudinary.com/demo/haisan.jpg');
SET @cat_seafood = LAST_INSERT_ID();

-- =============================================================================
-- 2. TẠO TÀI KHOẢN CHỦ CỬA HÀNG MỚI CHO QUÁN NƯỚNG & HẢI SẢN
-- =============================================================================
INSERT INTO users (phone_number, password, full_name, role, is_active) 
VALUES ('0909999999', '123456', 'Chủ Quán Nướng & Hải Sản', 'MERCHANT', TRUE);
SET @new_merchant = LAST_INSERT_ID();

-- =============================================================================
-- 3. TẠO CÁC CỬA HÀNG MỚI
-- =============================================================================
-- Quán Nướng BBQ
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders, rating_average, review_count) 
VALUES (@new_merchant, 'Vua Nướng BBQ - Thủ Đức', 'Võ Văn Ngân, Thủ Đức', '16:00:00', '23:00:00', 10.8500, 106.7520, 40, 4.8, 150);
SET @r_bbq = LAST_INSERT_ID();

-- Quán Hải Sản
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders, rating_average, review_count) 
VALUES (@new_merchant, 'Hải Sản Biển Đông - Thủ Đức', 'Tô Vĩnh Diện, Thủ Đức', '10:00:00', '22:30:00', 10.8460, 106.7590, 35, 4.7, 98);
SET @r_seafood = LAST_INSERT_ID();

-- =============================================================================
-- 4. THÊM MÓN ĂN CHO CÁC CỬA HÀNG MỚI
-- =============================================================================
-- Món ăn cho Vua Nướng BBQ (Thể loại: Đồ Nướng & BBQ)
INSERT INTO foods (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
(@r_bbq, @cat_bbq, 'Dẻ Sườn Bò Mỹ Nướng Sauce BBQ', 'Dẻ sườn bò Mỹ chọn lọc ướp nước sốt BBQ đặc trưng nướng chín mềm', 150000.00, 'https://res.cloudinary.com/demo/de_suon_bo.jpg', TRUE),
(@r_bbq, @cat_bbq, 'Ba Chỉ Bò Cuộn Nấm Kim Châm', 'Ba chỉ bò Mỹ thái mỏng cuộn nấm kim châm ngọt giòn', 120000.00, 'https://res.cloudinary.com/demo/ba_chi_cuon_nam.jpg', TRUE),
(@r_bbq, @cat_bbq, 'Nầm Heo Nướng Sa Tế', 'Nầm heo giòn sần sật ướp sa té cay nồng đậm vị', 95000.00, 'https://res.cloudinary.com/demo/nam_nuong.jpg', TRUE);

-- Món ăn cho Hải Sản Biển Đông (Thể loại: Hải Sản)
INSERT INTO foods (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
(@r_seafood, @cat_seafood, 'Tôm Hùm Sốt Bơ Tỏi', 'Tôm hùm tươi sống chiên giòn sốt bơ tỏi thơm ngậy', 350000.00, 'https://res.cloudinary.com/demo/tom_hum.jpg', TRUE),
(@r_seafood, @cat_seafood, 'Cua Cà Mau Hấp Sả', 'Cua Cà Mau chắc thịt hấp sả giữ trọn vị ngọt tự nhiên', 280000.00, 'https://res.cloudinary.com/demo/cua_camau.jpg', TRUE),
(@r_seafood, @cat_seafood, 'Nghêu Hấp Sả Ớt', 'Nghêu tươi hấp sả gừng ấm nóng, cay nhẹ kích thích vị giác', 65000.00, 'https://res.cloudinary.com/demo/ngheu_hap.jpg', TRUE);
