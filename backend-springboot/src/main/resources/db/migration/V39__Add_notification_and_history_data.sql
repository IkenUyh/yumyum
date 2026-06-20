-- V39__Add_notification_and_history_data.sql
-- Thêm dữ liệu cho thông báo và tất cả các tab trong lịch sử (đang đến, deal đã mua, lịch sử, đánh giá)

-- 1. Lấy ID người dùng mẫu
SET @user_huy = (SELECT id FROM users WHERE phone_number = '0987301126');
SET @user_dat = (SELECT id FROM users WHERE phone_number = '0376171242');

-- 2. Lấy ID tài xế mẫu
SET @driver_id = (SELECT id FROM users WHERE phone_number = '0999888777');

-- 3. Lấy ID nhà hàng mẫu
SET @res_com = (SELECT id FROM restaurants WHERE name = 'Cơm Tấm Phúc Lộc Thọ - Đinh Tiên Hoàng');
SET @res_banhmi = (SELECT id FROM restaurants WHERE name = 'Bánh Mì Huỳnh Hoa');
SET @res_sushi = (SELECT id FROM restaurants WHERE name = 'Sushi Hokkaido Sachi - Đồng Khởi');

-- 4. Lấy ID món ăn mẫu
SET @food_surn = (SELECT id FROM foods WHERE name = 'Cơm Sườn Nướng Mật Ong' AND restaurant_id = @res_com);
SET @food_bm_db = (SELECT id FROM foods WHERE name = 'Bánh Mì Đặc Biệt' AND restaurant_id = @res_banhmi);
SET @food_sashimi = (SELECT id FROM foods WHERE name = 'Sashimi Cá Hồi Tươi' AND restaurant_id = @res_sushi);
SET @food_sushi_combo = (SELECT id FROM foods WHERE name = 'Combo Sushi Mẫu' AND restaurant_id = @res_sushi);

-- 5. Thêm Voucher mới nếu chưa tồn tại
INSERT INTO vouchers (code, type, discount_percent, max_discount, min_order_value, stock_quantity, start_date, end_date, is_active)
VALUES 
('SEEDED_FREESHIP', 'SHIPPING_DISCOUNT', 50, 15000.00, 30000.00, 100, '2026-01-01 00:00:00', '2026-12-31 23:59:59', TRUE),
('SEEDED_FOODLOVER', 'ORDER_DISCOUNT', 15, 30000.00, 50000.00, 100, '2026-01-01 00:00:00', '2026-12-31 23:59:59', TRUE);

SET @v_freeship = (SELECT id FROM vouchers WHERE code = 'SEEDED_FREESHIP');
SET @v_foodlover = (SELECT id FROM vouchers WHERE code = 'SEEDED_FOODLOVER');

-- =============================================================================
-- TAB 1: ĐANG ĐẾN (PENDING / PREPARING / DELIVERING)
-- =============================================================================

-- Đơn hàng Đang Chờ Xác Nhận (PENDING) - Khách hàng: Huy Onii-chan
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, created_at)
VALUES (@user_huy, @res_com, 57000.00, 'PENDING', NULL, 15000.00, 0.00, 'STANDARD', DATE_ADD(NOW(), INTERVAL 40 MINUTE), NOW());
SET @order_pending_id = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order_pending_id, @food_surn, 1, 42000.00);

-- Đơn hàng Đang Chuẩn Bị (PREPARING) - Khách hàng: Huy Onii-chan
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, pickup_code, delivery_pin, created_at)
VALUES (@user_huy, @res_banhmi, 80000.00, 'PREPARING', NULL, 15000.00, 0.00, 'STANDARD', DATE_ADD(NOW(), INTERVAL 30 MINUTE), 'PICK555', 'PIN555', NOW());
SET @order_preparing_id = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order_preparing_id, @food_bm_db, 1, 65000.00);

-- Đơn hàng Đang Giao (DELIVERING) - Khách hàng: Huy Onii-chan
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, pickup_code, delivery_pin, created_at)
VALUES (@user_huy, @res_sushi, 400000.00, 'DELIVERING', @driver_id, 15000.00, 0.00, 'STANDARD', DATE_ADD(NOW(), INTERVAL 20 MINUTE), 'PICK777', 'PIN777', NOW());
SET @order_delivering_id = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order_delivering_id, @food_sashimi, 1, 165000.00);
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order_delivering_id, @food_sushi_combo, 1, 220000.00);


-- =============================================================================
-- TAB 2: DEAL ĐÃ MUA & TAB 3: LỊCH SỬ (COMPLETED) & TAB 4: ĐÁNH GIÁ (Chưa đánh giá, còn hạn)
-- =============================================================================

-- Đơn hàng Đã hoàn thành (COMPLETED), có áp dụng Voucher (xem ở tab Deal đã mua) và chưa đánh giá (chưa có trong reviews)
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, created_at)
VALUES (@user_huy, @res_com, 42000.00, 'COMPLETED', @driver_id, 15000.00, 15000.00, 'STANDARD', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR));
SET @order_voucher_id = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order_voucher_id, @food_surn, 1, 42000.00);

-- Liên kết voucher vào đơn hàng
INSERT INTO order_vouchers (order_id, voucher_id) VALUES (@order_voucher_id, @v_freeship);


-- =============================================================================
-- TAB 3: LỊCH SỬ (ĐÃ HỦY)
-- =============================================================================

-- Đơn hàng Đã hủy (CANCELLED) - Khách hàng: Huy Onii-chan
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, created_at, cancel_reason)
VALUES (@user_huy, @res_com, 57000.00, 'CANCELLED', NULL, 15000.00, 0.00, 'STANDARD', DATE_SUB(NOW(), INTERVAL 12 HOUR), 'Tôi muốn đổi địa chỉ giao hàng');
SET @order_cancelled_id = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order_cancelled_id, @food_surn, 1, 42000.00);


-- =============================================================================
-- TAB 3: LỊCH SỬ (ĐÃ HOÀN THÀNH, ĐÃ ĐÁNH GIÁ -> Không hiện trong TAB 4: ĐÁNH GIÁ)
-- =============================================================================

-- Đơn hàng Đã hoàn thành (COMPLETED) - Khách hàng: Huy Onii-chan
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, created_at)
VALUES (@user_huy, @res_banhmi, 80000.00, 'COMPLETED', @driver_id, 15000.00, 0.00, 'STANDARD', DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 11 HOUR));
SET @order_reviewed_id = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order_reviewed_id, @food_bm_db, 1, 65000.00);

-- Đánh giá đơn hàng này
INSERT INTO reviews (order_id, restaurant_id, rating, comment, created_at)
VALUES (@order_reviewed_id, @res_banhmi, 5, 'Bánh mì ngon xuất sắc, giao hàng siêu nhanh!', DATE_SUB(NOW(), INTERVAL 9 HOUR));


-- =============================================================================
-- TAB 3: LỊCH SỬ (ĐÃ HOÀN THÀNH, HẾT HẠN ĐÁNH GIÁ (>7 ngày) -> Không hiện trong TAB 4: ĐÁNH GIÁ)
-- =============================================================================

-- Đơn hàng Đã hoàn thành cách đây 9 ngày (COMPLETED) - Khách hàng: Huy Onii-chan
INSERT INTO orders (user_id, restaurant_id, total_amount, status, driver_id, shipping_fee, discount_amount, delivery_mode, expected_delivery_time, created_at)
VALUES (@user_huy, @res_com, 57000.00, 'COMPLETED', @driver_id, 15000.00, 0.00, 'STANDARD', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY));
SET @order_expired_id = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (@order_expired_id, @food_surn, 1, 42000.00);


-- =============================================================================
-- THÊM DỮ LIỆU THÔNG BÁO (NOTIFICATIONS)
-- =============================================================================

-- Thông báo cho Huy Onii-chan
INSERT INTO notifications (user_id, title, message, type, is_read, created_at)
VALUES 
(@user_huy, 'Chào mừng bạn đến với FoodDelivery!', 'Cảm ơn bạn đã đăng ký tài khoản. Chúc bạn có những trải nghiệm tuyệt vời cùng FoodDelivery!', 'SYSTEM', FALSE, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(@user_huy, 'Ưu đãi cực khủng: Khao 0đ Trà sữa!', 'Ưu đãi dành riêng cho bạn: Nhập mã NHAPMON để giảm ngay 100% phí vận chuyển cho các đơn trà sữa cuối tuần này.', 'PROMOTION', FALSE, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(@user_huy, 'Đơn hàng #12345 đã được giao thành công', 'Đơn hàng của bạn đặt tại Bánh Mì Huỳnh Hoa đã được giao thành công. Chúc bạn ngon miệng!', 'ORDER_UPDATE', TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@user_huy, 'Tài xế đang giao đơn hàng cho bạn', 'Tài xế Nguyễn Văn Tài Xế đang giao đơn hàng #54321 đến địa điểm của bạn. Vui lòng chuẩn bị nhận hàng.', 'ORDER_UPDATE', FALSE, DATE_SUB(NOW(), INTERVAL 5 MINUTE));

-- Thông báo cho Cao Dat
INSERT INTO notifications (user_id, title, message, type, is_read, created_at)
VALUES 
(@user_dat, 'Khuyến mãi đặc biệt mừng cuối tuần!', 'Tặng bạn mã GIAMGIA20 giảm ngay 20% tổng giá trị hóa đơn khi đặt món hôm nay.', 'PROMOTION', FALSE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(@user_dat, 'Đơn hàng đang chuẩn bị món', 'Đơn hàng đặt tại Cơm Tấm Phúc Lộc Thọ đang được nhà hàng chuẩn bị món.', 'ORDER_UPDATE', TRUE, DATE_SUB(NOW(), INTERVAL 30 MINUTE));


-- =============================================================================
-- THÊM DỮ LIỆU TIN TỨC & KHUYẾN MÃI (NEWS)
-- =============================================================================

INSERT INTO news (title, content, image_url, is_active, created_at)
VALUES
('Mừng khai trương, FoodDelivery khao bạn mã FREESHIP!', 'Nhân dịp ra mắt tính năng mới, FoodDelivery tặng khách hàng mã miễn phí vận chuyển lên đến 20.000đ cho mọi đơn hàng từ 50.000đ. Đặt món và trải nghiệm ngay dịch vụ giao đồ ăn siêu tốc từ chúng tôi nhé!', 'https://picsum.photos/seed/news_1/600/400', TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('Ăn sập Sài Gòn cùng ưu đãi giảm 15% tổng bill', 'Đặt món từ các thương hiệu được yêu thích nhất như Bánh Mì Huỳnh Hoa, Cơm Tấm Phúc Lộc Thọ để nhận ngay ưu đãi giảm 15% tổng hóa đơn. Số lượng voucher có hạn, nhanh tay săn ngay kẻo lỡ!', 'https://picsum.photos/seed/news_2/600/400', TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('Chào bạn mới: Tặng gói quà tặng trị giá 100K!', 'Món quà đặc biệt dành riêng cho người dùng mới đăng ký tài khoản trên ứng dụng. Nhận ngay gói voucher giảm giá cực sâu cho các đơn hàng đồ ăn và thức uống đầu tiên. Khám phá ngay tại mục Ưu Đãi.', 'https://picsum.photos/seed/news_3/600/400', TRUE, NOW()),
('Thông báo: Cập nhật điều khoản dịch vụ và chính sách tích xu', 'Nhằm nâng cao trải nghiệm người dùng, FoodDelivery chính thức cập nhật chính sách điểm danh nhận Xu thưởng. Kể từ ngày hôm nay, mỗi ngày điểm danh liên tục bạn sẽ nhận được thêm 50 xu thưởng tích lũy.', 'https://picsum.photos/seed/news_4/600/400', TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY));
