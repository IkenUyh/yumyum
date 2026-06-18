-- V16__Insert_mock_restaurants_and_foods.sql

-- =============================================================================
-- 1. TẠO CÁC TÀI KHOẢN CHỦ CỬA HÀNG (5 MERCHANTS)
-- =============================================================================
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0901111111', '123456', 'Nguyễn Chủ Quán Quận 1', 'MERCHANT', TRUE);
SET @m1 = LAST_INSERT_ID();
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0902222222', '123456', 'Trần Chủ Quán Quận 3', 'MERCHANT', TRUE);
SET @m2 = LAST_INSERT_ID();
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0903333333', '123456', 'Lê Chủ Quán Quận 10', 'MERCHANT', TRUE);
SET @m3 = LAST_INSERT_ID();
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0904444444', '123456', 'Phạm Chủ Quán Bình Thạnh', 'MERCHANT', TRUE);
SET @m4 = LAST_INSERT_ID();
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0905555555', '123456', 'Hoàng Chủ Quán Thủ Đức', 'MERCHANT', TRUE);
SET @m5 = LAST_INSERT_ID();

-- =============================================================================
-- 2. TẠO DANH MỤC MÓN ĂN (10 CATEGORIES)
-- =============================================================================
INSERT INTO categories (name, image_url) VALUES ('Cơm', 'https://res.cloudinary.com/demo/com.jpg'); SET @cat1 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Bánh Mì', 'https://res.cloudinary.com/demo/banhmi.jpg'); SET @cat2 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Phở & Bún', 'https://res.cloudinary.com/demo/pho_bun.jpg'); SET @cat3 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Gà Rán & Fastfood', 'https://res.cloudinary.com/demo/fastfood.jpg'); SET @cat4 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Trà Sữa', 'https://res.cloudinary.com/demo/trasua.jpg'); SET @cat5 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Cà Phê & Đồ Uống', 'https://res.cloudinary.com/demo/cafe.jpg'); SET @cat6 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Ăn Vặt', 'https://res.cloudinary.com/demo/anvat.jpg'); SET @cat7 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Món Nhật & Hàn', 'https://res.cloudinary.com/demo/nhathan.jpg'); SET @cat8 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Ý & Pizza', 'https://res.cloudinary.com/demo/pizza.jpg'); SET @cat9 = LAST_INSERT_ID();
INSERT INTO categories (name, image_url) VALUES ('Lẩu & Dimsum', 'https://res.cloudinary.com/demo/laudimsum.jpg'); SET @cat10 = LAST_INSERT_ID();

-- =============================================================================
-- 3. TẠO DANH SÁCH 25 CỬA HÀNG (RESTAURANTS)
-- =============================================================================
-- Nhóm Merchant 1 (Quận 1)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m1, 'Cơm Tấm Phúc Lộc Thọ - Đinh Tiên Hoàng', 'Đinh Tiên Hoàng, Quận 1', '06:00:00', '22:00:00', 10.7872, 106.6995, 30); SET @r1 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m1, 'Bánh Mì Huỳnh Hoa', 'Lê Thị Riêng, Quận 1', '14:00:00', '23:00:00', 10.7719, 106.6914, 50); SET @r2 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m1, 'Sushi Hokkaido Sachi - Đồng Khởi', 'Đồng Khởi, Quận 1', '10:30:00', '22:45:00', 10.7758, 106.7029, 25); SET @r3 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m1, 'Bún Mọc 111 Ngô Thì Nhậm - Chi Nhánh 1', 'Nguyễn Du, Quận 1', '07:00:00', '21:00:00', 10.7742, 106.6951, 20); SET @r4 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m1, 'Lẩu Haidilao - Bitexco', 'Hải Triều, Quận 1', '09:00:00', '02:00:00', 10.7715, 106.7042, 60); SET @r5 = LAST_INSERT_ID();

-- Nhóm Merchant 2 (Quận 3)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m2, 'Phở Thìn Hà Nội - Cao Thắng', 'Cao Thắng, Quận 3', '06:00:00', '21:30:00', 10.7712, 106.6798, 30); SET @r6 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m2, 'Hủ Tiếu Nam Vang Thành Đạt', 'Lý Thái Tổ, Quận 3', '00:00:00', '23:59:59', 10.7675, 106.6782, 40); SET @r7 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m2, 'Bún Chả Hà Nội 1986', 'Nguyễn Thị Minh Khai, Quận 3', '08:00:00', '21:00:00', 10.7815, 106.6922, 25); SET @r8 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m2, 'Bánh Xèo Ăn Là Nghiền', 'Sương Nguyệt Ánh, Quận 3', '09:00:00', '22:00:00', 10.7731, 106.6885, 20); SET @r9 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m2, 'Gà Rán Popeyes - Nguyễn Thị Minh Khai', 'Nguyễn Thị Minh Khai, Quận 3', '09:00:00', '22:00:00', 10.7745, 106.6854, 35); SET @r10 = LAST_INSERT_ID();

-- Nhóm Merchant 3 (Quận 10)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m3, 'Trà Sữa Koi Thé - Sư Vạn Hạnh', 'Sư Vạn Hạnh, Quận 10', '09:00:00', '22:00:00', 10.7723, 106.6689, 45); SET @r11 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m3, 'Bún Đậu Mắm Tôm A Chảnh', 'Thành Thái, Quận 10', '09:30:00', '22:00:00', 10.7751, 106.6624, 40); SET @r12 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m3, 'Tokbokki Saigon - Đường 3/2', 'Đường 3 Tháng 2, Quận 10', '10:00:00', '21:30:00', 10.7708, 106.6735, 30); SET @r13 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m3, 'Chè Thái Ý Phương', 'Nguyễn Tri Phương, Quận 10', '10:00:00', '23:00:00', 10.7612, 106.6661, 55); SET @r14 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m3, 'Súp Cua Hạnh - Nguyễn Tri Phương', 'Nguyễn Tri Phương, Quận 10', '06:30:00', '22:30:00', 10.7634, 106.6655, 35); SET @r15 = LAST_INSERT_ID();

-- Nhóm Merchant 4 (Bình Thạnh)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m4, 'Cơm Tấm Long Xuyên - Xô Viết Nghệ Tĩnh', 'Xô Viết Nghệ Tĩnh, Bình Thạnh', '06:00:00', '20:00:00', 10.8021, 106.7085, 25); SET @r16 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m4, 'The Coffee House - D5', 'Đường D5, Bình Thạnh', '07:00:00', '22:30:00', 10.8015, 106.7121, 40); SET @r17 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m4, 'Bún Bò Huế Đông Ba - Phan Đăng Lưu', 'Phan Đăng Lưu, Bình Thạnh', '06:00:00', '22:00:00', 10.8001, 106.6918, 30); SET @r18 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m4, 'Pizza Company - Nguyễn Gia Trí', 'Nguyễn Gia Trí, Bình Thạnh', '10:00:00', '22:00:00', 10.8034, 106.7115, 30); SET @r19 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m4, 'Gà Rán KFC - Bạch Đằng', 'Bạch Đằng, Bình Thạnh', '09:00:00', '22:00:00', 10.8012, 106.6988, 35); SET @r20 = LAST_INSERT_ID();

-- Nhóm Merchant 5 (Thủ Đức / Làng Đại Học)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m5, 'Highlands Coffee - Võ Văn Ngân', 'Võ Văn Ngân, Thủ Đức', '06:30:00', '23:00:00', 10.8492, 106.7534, 50); SET @r21 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m5, 'Cơm Gà Hải Nam - Hàn Thuyên', 'Hàn Thuyên, Làng Đại Học', '09:00:00', '21:00:00', 10.8745, 106.8012, 30); SET @r22 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m5, 'Quán Ăn Vặt Bé Bi - ĐHQG', 'Đường nội bộ ĐHQG, Thủ Đức', '13:00:00', '22:00:00', 10.8721, 106.8035, 40); SET @r23 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m5, 'Dimsum Tiến Phát - Chi Nhánh Thủ Đức', 'Tô Vĩnh Diện, Thủ Đức', '06:00:00', '12:30:00', 10.8465, 106.7588, 20); SET @r24 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m5, 'Cá Viên Chiên Nước Mắm LHK - Thủ Đức', 'Dân Chủ, Thủ Đức', '15:00:00', '23:00:00', 10.8478, 106.7601, 35); SET @r25 = LAST_INSERT_ID();


-- =============================================================================
-- 4. TẠO 150 MÓN ĂN (FOODS - MỖI QUÁN ĐÚNG 6 MÓN)
-- =============================================================================
INSERT INTO foods (restaurant_id, category_id, name, description, price,image_url) VALUES
-- R1: Cơm Tấm Phúc Lộc Thọ
(@r1, @cat1, 'Cơm Sườn Nướng Mật Ong', 'Sườn cốt lết nướng mật ong thơm phức', 42000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781697443/C%C6%A1m_t%E1%BA%A5m_S%C3%A0i_G%C3%B2n_m%C3%B3n_ngon_kh%C3%B4ng_th%E1%BB%83_t%C3%ACm_%E1%BB%9F_b%E1%BA%A5t_k%E1%BB%B3_n%C6%A1i_n%C3%A0o_szbkne.jpg'),
(@r1, @cat1, 'Cơm Sườn Bì Chả', 'Combo truyền thống đầy đủ năng lượng', 55000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781697443/C%C6%A1m_t%E1%BA%A5m_S%C3%A0i_G%C3%B2n_m%C3%B3n_ngon_kh%C3%B4ng_th%E1%BB%83_t%C3%ACm_%E1%BB%9F_b%E1%BA%A5t_k%E1%BB%B3_n%C6%A1i_n%C3%A0o_szbkne.jpg'),
(@r1, @cat1, 'Cơm Ba Rọi Nướng Sả', 'Thịt ba rọi giòn bì đượm hương sả', 45000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781698290/d6a43762-5b26-4fcc-a5fa-a3c1f10a74cc.png'),
(@r1, @cat1, 'Cơm Đùi Gà Nướng', 'Đùi gà góc tư ướp ngũ vị hương', 50000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781697443/C%C6%A1m_t%E1%BA%A5m_S%C3%A0i_G%C3%B2n_m%C3%B3n_ngon_kh%C3%B4ng_th%E1%BB%83_t%C3%ACm_%E1%BB%9F_b%E1%BA%A5t_k%E1%BB%B3_n%C6%A1i_n%C3%A0o_szbkne.jpg'),
(@r1, @cat1, 'Canh Khổ Qua Nhồi Thịt', 'Canh giải nhiệt ăn kèm cơm tấm', 15000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781698655/06b784df-f544-4d8d-894c-24f6d31baa5d.png'),
(@r1, @cat1, 'Chả Trứng Thêm', 'Một bánh chả trứng chưng truyền thống', 10000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781698750/5b5bb47a-7611-4fbd-adef-fb31b4a8e6e2.png'),

-- R2: Bánh Mì Huỳnh Hoa
(@r2, @cat2, 'Bánh Mì Đặc Biệt', 'Ổ bánh mì ổ nặng đô đầy đủ các loại chả, pate, bơ', 65000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781698837/Banh-mi-huynh-hoa-topping-scaled_iqayrp.jpg'),
(@r2, @cat2, 'Bánh Mì Chả Lụa Xá Xíu', 'Nhân thịt xá xíu đậm đà kết hợp chả lụa hảo hạng', 50000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781698921/banh-mi-pate-cha-lua_eruert.jpg'),
(@r2, @cat2, 'Bánh Mì Pate Bơ Siêu Béo', 'Dành riêng cho tín đồ mê sốt bơ và pate Huỳnh Hoa', 40000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699002/download_2_mgoyny.jpg'),
(@r2, @cat2, 'Ruốc Chà Bông Gà (Hộp 200g)', 'Chà bông gà nhà làm, dai ngon đậm vị', 60000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699149/4d046aff-fe44-4d08-96c6-a1c571fa2592.png'),
(@r2, @cat2, 'Pate Thượng Hạng (Hộp 100g)', 'Hộp pate chuẩn vị làm nên thương hiệu', 45000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699222/f8b7d150-4306-4f2f-b008-24aa9bc08962.png'),
(@r2, @cat2, 'Ổ Bánh Mì Không Giòn Rụm', 'Bánh mì đặc ruột nướng nóng giòn', 70000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699403/679fcb43-fb74-429a-8381-cb16c263de9d.png'),

-- R3: Sushi Hokkaido Sachi
(@r3, @cat8, 'Sashimi Cá Hồi Tươi', '5 lát cá hồi tươi sống nhập khẩu từ Na-uy', 165000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699663/10-1200x676-6_rmk7kn.jpg'),
(@r3, @cat8, 'Combo Sushi Mẫu', 'Tổng hợp sushi tôm, cá hồi, trứng cá chuồn', 220000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699763/50c2d82f-1522-422a-ad8b-eaa0e2a6b49c.png'),
(@r3, @cat8, 'Mì Udon Hải Sản Hotpot', 'Mì udon sợi dày nấu cùng tôm, mực, nghêu', 145000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699841/740325ef-6103-4d2b-9af5-264857057aa2.png'),
(@r3, @cat8, 'Cơm Lươn Nhật Bản (Unadon)', 'Lươn nướng sốt Kabayaki đậm đà phủ trên cơm', 280000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699905/5b0c6ba8-9181-4244-9ce5-29c7c5512473.png'),
(@r3, @cat8, 'Tempura Tôm Sú', '3 con tôm sú chiên xù kiểu Nhật giòn rụm', 95000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781700423/d5aa5c8b-54e5-4799-96ee-29440a896186.png'),
(@r3, @cat8, 'Súp Miso Truyền Thống', 'Súp rong biển nấu cùng đậu hũ non thanh mát', 30000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781700494/4fbca6da-a376-46ca-982f-78604b8c9593.png'),

-- R4: Bún Mọc 111 Ngô Thì Nhậm
(@r4, @cat3, 'Bún Mọc Sườn Non', 'Sườn heo ninh nhừ kết hợp mọc tươi dai giòn', 55000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781700660/dafef3a1-cd75-4c96-b2d7-19c39646bdd3.png'),
(@r4, @cat3, 'Bún Mọc Đặc Biệt Ngô Thì Nhậm', 'Đầy đủ mọc chiên, mọc hấp, giò tai và sườn', 65000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781700777/4e0fa299-1db3-4b86-9a5d-d7c1274f8e86.png'),
(@r4, @cat3, 'Bún Mọc Dọc Mùng', 'Nước dùng thanh ngọt ăn kèm dọc mùng giòn sần sật', 50000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781700921/a6c4949f-b5a1-4098-b082-d0422d3bde1c.png'),
(@r4, @cat3, 'Mọc Hấp Ăn Thêm (Chén 3 viên)', 'Viên mọc hấp truyền thống thơm mùi tiêu', 15000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781701134/387fec84-e274-485f-9461-59090bc62477.png'),
(@r4, @cat3, 'Mọc Chiên Vàng (Chén 3 viên)', 'Mọc chiên thơm béo ngậy cực cuốn', 15000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781701439/391509b1-73b7-41e2-9ea3-cbd623e800a0.png'),
(@r4, @cat3, 'Chả Lụa Gói Lá Chuối', 'Cây chả lụa ăn kèm tăng thêm hương vị', 12000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781701534/7cb93744-ef49-47fe-ba72-f404c9ae2b30.png'),

-- R5: Lẩu Haidilao
(@r5, @cat10, 'Nước Cốt Lẩu Súp Cay Tứ Xuyên', 'Hương vị cay nồng chuẩn vị Haidilao', 90000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781701746/32f5437d-6001-4fd2-a378-09f9001bc453.png'),
(@r5, @cat10, 'Nước Cốt Lẩu Nấm Bổ Dưỡng', 'Thơm mùi nấm đông cô và thảo mộc thanh đạm', 80000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781701826/60ba2b16-b9d6-4259-9c06-dc8bdd9efbbb.png'),
(@r5, @cat10, 'Thịt Bò Haidilao Thượng Hạng', 'Thịt bò tươi thái lát mỏng mềm tan', 185000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781701931/8bdd8d44-f2c6-4cc9-97f1-c201d1036ad7.png'),
(@r5, @cat10, 'Đậu Hũ Phô Mai Chiên', 'Hộp 6 viên đậu hũ béo ngậy tan chảy', 65000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781702017/63b540bc-02ed-430b-936d-7e8590591938.png'),
(@r5, @cat10, 'Mì Tươi Biểu Diễn Haidilao', 'Suất mì tươi làm thủ công tại bàn', 40000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781702521/2072c2e1-9289-4298-a8ec-da8fdb9abb8f.png'),
(@r5, @cat10, 'Vò Viên Tôm Hoàng Kim', 'Tôm tươi xay nhuyễn bọc trứng muối siêu ngon', 120000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781702690/d9d6daa5-b427-4238-a17a-c0440e9c207c.png'),

-- R6: Phở Thìn Hà Nội
(@r6, @cat3, 'Phở Tái Lăn Truyền Thống', 'Thịt bò xào tái lăn nhanh với tỏi và hành lá ngập tràn', 65000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781702895/download_1_cn0ook.jpg'),
(@r6, @cat3, 'Phở Nạm Gầu Giòn', 'Sự kết hợp hoàn hảo giữa nạm mềm và gầu giòn', 60000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781703168/food_hc2rfz.jpg'),
(@r6, @cat3, 'Phở Gà Ta Xé Phay', 'Thịt gà ta da giòn, nước dùng thanh ngọt', 55000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781702981/download_7_vflqyh.jpg'),
(@r6, @cat3, 'Quẩy Nóng Giòn', 'Đĩa 3 chiếc quẩy ăn kèm phở chuẩn vị Bắc', 10000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781703248/d7bdc233-4236-4296-8c4c-a207539221b8.png'),
(@r6, @cat3, 'Trứng Chần Sốt Nước Tiết', 'Chén trứng chần bổ dưỡng cho bữa sáng', 12000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781703358/8b90d550-642b-40bd-bf93-4d26b651df81.png'),
(@r6, @cat3, 'Phở Đặc Biệt (Tái, Nạm, Gầu, Trứng)', 'Tô phở khổng lồ full topping nâng cấp', 85000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781703476/download_5_g6rfpb.jpg"),

-- R7: Hủ Tiếu Nam Vang Thành Đạt
(@r7, @cat3, 'Hủ Tiếu Khô Đặc Biệt', 'Hủ tiếu trộn sốt hắc xì dầu kèm chén súp sườn tinh túy', 55000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781703678/H%E1%BB%A7_ti%E1%BA%BFu_nam_vang_1_lrwn92.jpg'),
(@r7, @cat3, 'Hủ Tiếu Nước Đầy Đủ', 'Nước dùng ngọt xương ống, tôm, mực, trứng cút', 50000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781703740/Hu_Tieu_Nam_Vang_Vietnamese_Pork_and_Prawn_Clear_Noodle_Soup_i0e3me.jpg'),
(@r7, @cat3, 'Hủ Tiếu Mì Sườn Kho', 'Sợi mì trứng dai mềm hầm cùng sườn heo bản lớn', 60000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781703983/77904784-6a2c-4563-8834-45da403a4561.png'),
(@r7, @cat3, 'Hủ Tiếu Mực Tươi Khô', 'Mực ống tươi roi rói trụng vừa chín tới', 55000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781704195/8730a091-a481-4a10-b68f-23419fba9d30.png'),
(@r7, @cat3, 'Súp Hoành Thánh Tôm Thịt', 'Hoành thánh gói tay ngập nhân thịt tôm bằm', 35000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781704319/8ce20cad-00fc-4b22-afd9-31ff26347fa1.png''),
(@r7, @cat3, 'Xí Quách Heo Thêm', 'Khúc xương ống tủy lớn kèm nước lèo', 30000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781704465/73a9b37c-9547-4598-8d6e-9ba317aa010d.png'),

-- R8: Bún Chả Hà Nội 1986
(@r8, @cat3, 'Mẹt Bún Chả Đặc Biệt', 'Gồm chả băm, chả miếng nướng than hoa, bún và rau sống', 55000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781704697/B%C3%BAn_ch%E1%BA%A3_H%C3%A0_N%E1%BB%99i_zf271b.jpg'),
(@r8, @cat3, 'Bún Chả Nem Cua Bể', 'Combo gồm bún chả truyền thống và 1 chiếc nem cua bể', 75000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781705704/044eeabc-e2f3-4cb9-8049-68889cc63152.png'),
(@r8, @cat3, 'Nem Cua Bể Giòn Rụm (1 chiếc)', 'Nem vuông nhân thịt ghẹ và cua biển đậm vị', 25000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781705817/049ebfb7-b8b5-4fc0-a361-a3510bc2bd25.png'),
(@r8, @cat3, 'Thịt Ba Chỉ Nướng Thêm', 'Suất thịt nướng cháy cạnh ăn thêm', 25000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781705985/31067674-0f5f-4cc4-8e92-02dbb12d2c10.png'),
(@r8, @cat3, 'Chả Băm Cuốn Lá Lốt Thêm', 'Suất 3 viên chả băm thơm lừng', 20000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781706169/94d447a9-41c7-4b80-a718-829d78aec754.png'),
(@r8, @cat3, 'Nước Sấu Đá Hà Nội', 'Thức uống giải nhiệt chua ngọt thanh mát', 15000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781706306/68afdcee-3686-40c2-8bfe-f38090690d5f.png'),

-- R9: Bánh Xèo Ăn Là Nghiền
(@r9, @cat7, 'Bánh Xèo Tôm Nhảy Đất Đỏ', 'Nhân tôm đất tươi rói, giá đỗ, thịt ba rọi', 70000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781706670/a85c51f7-5507-4725-9038-7e3ed6e509fe.png'),
(@r9, @cat7, 'Bánh Xèo Nấm Đùi Gà Đồ Chay', 'Lựa chọn thanh đạm với nấm và đậu xanh', 60000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781707020/11d4db3a-8901-4c65-8690-ec8746d89797.png'),
(@r9, @cat7, 'Bánh Khọt Vũng Tàu (Đĩa 8 cái)', 'Bánh khọt tôm béo ngậy nước cốt dừa', 55000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781707182/027f8eb3-6485-4950-b553-972d30b0e988.png'),
(@r9, @cat7, 'Rau Rừng Tây Ninh Ăn Kèm', 'Mẹt rau rừng đủ loại cuốn bánh xèo', 15000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781707384/52dfceff-cf71-4a93-9cf1-146a5e24448e.png'),
(@r9, @cat7, 'Bánh Tráng Phơi Sương Cuốn Thêm', 'Xấp bánh tráng dẻo dai cao cấp', 10000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781707544/59843d10-2c45-459a-9a3b-5194ab0e99ae.png'),
(@r9, @cat7, 'Nước Mắm Chua Ngọt Bí Truyền', 'Chai nước mắm pha sẵn chuẩn vị', 10000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781707611/b1033611-f389-42a9-8f51-1284fd343bad.png'),

-- R10: Gà Rán Popeyes
(@r10, @cat4, '2 Miếng Gà Giòn Sả Ớt', 'Gà rán da giòn đậm đà hương vị sả ớt Việt Nam', 76000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781707758/4acff3e208a92d15e76113e166ece336_khyzkw.jpg'),
(@r10, @cat4, '3 Miếng Gà Không Xương (Tenders)', 'Ức gà tẩm bột chiên phi-lê dễ ăn', 62000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781707811/4b7305d2942b44bb0b4d91ba5c1a3207_xwtlxf.jpg'),
(@r10, @cat4, 'Burger Gà Phi-lê Thượng Hạng', 'Burger nhân gà giòn, sốt mayo và xà lách', 49000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781707963/d935099c-d0bf-4ec4-9d76-c37b7a54d28a.png'),
(@r10, @cat4, 'Khoai Tây Chiên Cỡ Lớn', 'Khoai tây tẩm gia vị Cajun độc quyền', 35000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781708041/0ccdfd7eddecb6e7e1b10ecd78ed69bd_plvvlf.jpg'),
(@r10, @cat4, 'Bánh Quy Mật Ong (Biscuit)', 'Bánh nướng bơ sữa phủ mật ong ngọt dịu', 15000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781708181/ddcb1350-11ea-47c8-af9b-2eba1f80c53f.png'),
(@r10, @cat4, 'Ly Pepsi Tươi Mát Lạnh', 'Nước ngọt giải khát ăn kèm gà rán', 18000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781708285/e22a1ab263be7d5b327e91880d6f8174_wb6xmo.jpg'),

-- R11: Trà Sữa Koi Thé
(@r11, @cat5, 'Trà Sữa Trân Châu Hoàng Kim', 'Trà sữa signature kết hợp trân châu hoàng kim dai giòn', 65000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781708443/f1d22f0f-ff9a-4a87-9ba6-a94d1dc09656.png'),
(@r11, @cat5, 'Lục Trà Macchiato', 'Trà xanh thanh mát phủ lớp kem sữa mặn béo ngậy', 60000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781708579/8f168c39-6207-4526-94d3-71aefbee3559.png'),
(@r11, @cat5, 'Ovaltine Macchiato', 'Thức uống sô-cô-la lúa mạch kết hợp kem béo', 65000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781709032/f6cfe27d-fac5-43d0-9be6-f79f023599e3.png'),
(@r11, @cat5, 'Trà Sữa Sương Sáo Cắt Nhỏ', 'Sương sáo thanh mát hòa quyện cốt trà sữa', 58000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781717859/b5df1f6b-ca8e-4e4b-ad7c-92401ffd1c8f.png'),
(@r11, @cat5, 'Trà Xanh Sữa Konjac', 'Hương vị trà xanh nhài nhẹ nhàng cùng thạch giòn', 60000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781717940/f2319e99-0afb-47e3-aa4c-6e4b6daf0356.png'),
(@r11, @cat5, 'Trà Đen Cốt Macchiato', 'Hương vị trà đen đậm đà kèm kem sữa béo', 60000.00,'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781718043/9c25c55f-280a-45ee-80b4-2655322a022e.png'),

-- R12: Bún Đậu Mắm Tôm A Chảnh
(@r12, @cat3, 'Mẹt Bún Đậu Đầy Đủ', 'Đậu hũ chiên giòn, thịt luộc, chả cốm, nem chua rán, dồi chiên', 65000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719027/food/met_bun_dau_day_du_1781719026.jpg'),
(@r12, @cat3, 'Mẹt Bún Đậu Chả Cốm Phổ Thông', 'Dành cho người ăn đơn giản với đậu và chả cốm', 45000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719030/food/met_bun_dau_cha_com_pho_thong_1781719030.jpg'),
(@r12, @cat3, 'Nem Chua Rán Hà Nội (Đĩa 4 chiếc)', 'Nem chua tẩm bột chiên xù ráo dầu', 30000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719035/food/nem_chua_ran_ha_noi_dia_4_chiec_1781719032.jpg'),
(@r12, @cat3, 'Dồi Sụn Nướng Thơm', 'Dồi heo nhân sụn sần sật nướng vàng', 35000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719040/food/doi_sun_nuong_thom_1781719038.jpg'),
(@r12, @cat3, 'Chả Cốm Chiên Nóng Thêm', '1 khoanh chả cốm dẻo thơm nồng nàn', 15000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719043/food/cha_com_chien_nong_them_1781719043.jpg'),
(@r12, @cat3, 'Nước Mơ Ngâm Đường Phố Cổ', 'Nước giải khát vị chua mặn ngọt hài hòa', 15000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719045/food/nuoc_mo_ngam_duong_pho_co_1781719045.webp'),
-- R13: Tokbokki Saigon
(@r13, @cat8, 'Lẩu Tokbokki Truyền Thống', 'Nồi lẩu gồm bánh gạo, chả cá Hàn Quốc, mì ramen và sốt cay', 139000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781717391/food/lau_tokbokki_truyen_thong_1781717389.jpg'),
(@r13, @cat8, 'Tokbokki Phô Mai Kéo Sợi', 'Bánh gạo xào sốt cay phủ ngập phô mai Mozzarella', 69000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781717529/d9c3cba5-2b48-412b-88e8-70d46dc58963.png'),
(@r13, @cat8, 'Gà Rán Sốt Gia Vị Sweet & Spicy', 'Gà rút xương chiên giòn đẫm sốt ngọt cay', 89000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781717400/food/ga_ran_sot_gia_vi_sweet__spicy_1781717400.jpg'),
(@r13, @cat8, 'Miến Trộn Hàn Quốc Japchae', 'Miến dai trộn dầu mè, rau củ và thịt bò thái sợi', 59000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781717407/food/mien_tron_han_quoc_japchae_1781717406.jpg'),
(@r13, @cat8, 'Kimbap Truyền Thống', 'Cơm cuộn rong biển nhân trứng, xúc xích, dưa leo', 39000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781717413/food/kimbap_truyen_thong_1781717412.jpg'),
(@r13, @cat8, 'Chả Cá Xiên Nước Súp (3 xiên)', 'Chả cá Odeng ngập trong nước dùng thanh thanh', 30000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781717671/cha-ca-han-quoc-la-gi-mua-o-dau-4-mon-ngon-tu-cha-ca-han-quoc-202103111105338988_cpd0pn.jpg'),
-- R14: Chè Thái Ý Phương
(@r14, @cat7, 'Chè Thái Đặc Biệt Siêu Sầu Riêng', 'Chè trái cây cốt dừa kèm múi sầu riêng tươi nguyên chất', 45000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781713635/food/che_thai_dac_biet_sieu_sau_rieng_1781713632.webp'),
(@r14, @cat7, 'Chè Khúc Bạch Truyền Thống', 'Khúc bạch bơ sữa, hạnh nhân lát và quả vải ngọt', 35000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781713639/food/che_khuc_bach_truyen_thong_1781713638.jpg'),
(@r14, @cat7, 'Sữa Chua Trái Cây Tô', 'Sữa chua nhà làm mít, xoài, bơ, hạt đác', 40000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719165/food/sua_chua_trai_cay_to_1781719163.jpg'),
(@r14, @cat7, 'Bánh Flan Nước Cốt Dừa (Cặp 2 cái)', 'Bánh flan mềm mịn đắng nhẹ vị cà phê', 24000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719169/food/banh_flan_nuoc_cot_dua_cap_2_cai_1781719167.jpg'),
(@r14, @cat7, 'Gỏi Cuốn Tôm Thịt (Dĩa 3 cuốn)', 'Đồ ăn kèm siêu hot tại quán ăn cùng sốt tương đậu', 30000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719173/food/goi_cuon_tom_thit_dia_3_cuon_1781719171.jpg'),
(@r14, @cat7, 'Bánh Tráng Trộn Sợi Tây Ninh', 'Bánh tráng trộn bò khô, mực xé, trứng cút', 25000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719175/food/banh_trang_tron_soi_tay_ninh_1781719175.jpg'),

-- R15: Súp Cua Hạnh
(@r15, @cat7, 'Súp Cua Trứng Bắc Thảo', 'Súp cua đặc sánh kèm nguyên quả trứng bách thảo bùi bùi', 35000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781711786/food/sup_cua_trung_bac_thao_1781711782.jpg'),
(@r15, @cat7, 'Súp Cua Bong Bóng Cá Cao Cấp', 'Súp nấu cùng thịt cua tuyết và bong bóng cá dầy dặn', 45000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781711790/food/sup_cua_bong_bong_ca_cao_cap_1781711788.jpg'),
(@r15, @cat7, 'Súp Cua Óc Heo Trứng Cút', 'Óc heo tươi làm sạch chưng cách thủy bổ dưỡng', 40000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781711794/food/sup_cua_oc_heo_trung_cut_1781711792.jpg'),
(@r15, @cat7, 'Chén Tủy Heo Chưng Thêm', 'Tủy béo ngậy ăn kèm súp tăng vị đậm đà', 20000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781711798/food/chen_tuy_heo_chung_them_1781711796.jpg'),
(@r15, @cat7, 'Hột Vịt Bắc Thảo Thêm', '1 quả trứng bách thảo ăn thêm', 10000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781711800/food/hot_vit_bac_thao_them_1781711800.jpg'),
(@r15, @cat7, 'Cồi Sò Điệp Trụng Súp', '3 cồi sò điệp tươi ngon giòn ngọt', 25000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781711803/food/coi_so_diep_trung_sup_1781711801.jpg'),
-- R16: Cơm Tấm Long Xuyên
(@r16, @cat1, 'Cơm Tấm Nhuyễn Thịt Kho Trứng Kho', 'Cơm tấm nhuyễn hạt nhỏ, thịt kho xắt hạt lựu đặc trưng', 40000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781719653/-2128-1664160638_xcxzwa.webp'),
(@r16, @cat1, 'Cơm Tấm Bì Chả Miền Tây', 'Hương vị chả chưng miền Tây béo bùi', 35000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719356/food/com_tam_bi_cha_mien_tay_1781719354.jpg'),
(@r16, @cat1, 'Thịt Kho Cắt Nhỏ Gọi Thêm', 'Suất thịt kho băm nhỏ ăn kèm nước sốt kẹo', 15000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781719711/thit-khia-05102022-2057-1704514997465-17045149976591229536915_xswy4m.webp'),
(@r16, @cat1, 'Trứng Vịt Kho Lòng Đào', 'Trứng kho thấm vị nước dừa tươi', 80000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719365/food/trung_vit_kho_long_dao_1781719364.jpg'),
(@r16, @cat1, 'Canh Cải Ngọt Thịt Bằm', 'Canh ăn kèm cơm tấm nóng hổi', 12000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719370/food/canh_cai_ngot_thit_bam_1781719368.jpg'),
(@r16, @cat1, 'Dưa Đu Đủ Ngâm Chua Ngọt', 'Đồ chua ăn kèm chống ngán chuẩn vị miền Tây', 5000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719373/food/dua_du_du_ngam_chua_ngot_1781719373.jpg'),

-- R17: The Coffee House
(@r17, @cat6, 'Cà Phê Sữa Đá Nhà Làm', 'Đậm đà hạt cà phê Robusta rang xay nguyên chất', 39000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719478/food/ca_phe_sua_da_nha_lam_1781719476.jpg'),
(@r17, @cat6, 'Trà Đào Đá Xay Phúc Bồn Tử', 'Trà đào xay mát lạnh hòa quyện mứt phúc bồn tử', 59000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719482/food/tra_dao_da_xay_phuc_bon_tu_1781719481.jpg'),
(@r17, @cat6, 'Trà Thạch Vải Cao Cấp', 'Cốt trà lài thanh mát cùng hạt vải ngâm giòn', 55000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781719974/2-2_p1tlks.png'),
(@r17, @cat6, 'Frosty Sô-cô-la Đá Xay', 'Thức uống đá xay đậm vị hạt cacao nguyên chất', 65000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719490/food/frosty_socola_da_xay_1781719489.jpg'),
(@r17, @cat6, 'Bánh Mì Que Paris', 'Bánh mì que nhân pate gan nướng giòn', 19000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781719494/food/banh_mi_que_paris_1781719493.jpg'),
(@r17, @cat6, 'Bánh Mousse Gấu Socola', 'Bánh ngọt hình chú gấu dễ thương, cốt bánh mềm mại', 39000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781720035/images_hs4yn2.jpg'),

-- R18: Bún Bò Huế Đông Ba
(@r18, @cat3, 'Bún Bò Tái Nạm Bản Lớn', 'Bắp bò tái tươi hoa kết hợp nạm bò ninh mềm', 55000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720197/food/bun_bo_tai_nam_ban_lon_1781720195.webp'),
(@r18, @cat3, 'Bún Bò Gân Chả Cua', 'Gân bò dai sần sật và viên chả cua xứ Huế chính gốc', 60000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720201/food/bun_bo_gan_cha_cua_1781720199.jpg'),
(@r18, @cat3, 'Bún Bò Huế Đặc Biệt Giò Heo', 'Tô đầy đủ có khoanh giò heo nạc lớn đại ca', 75000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720203/food/bun_bo_hue_dac_biet_gio_heo_1781720204.jpg'),
(@r18, @cat3, 'Chả Cua Huế Gọi Thêm (2 viên)', 'Viên chả cua quét tay dai ngon thơm ngọt', 16000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720208/food/cha_cua_hue_goi_them_2_vien_1781720206.jpg'),
(@r18, @cat3, 'Chén Tiết Heo Luộc Súp', 'Tiết heo mềm mịn như thạch không bị xốp', 8000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720211/food/chen_tiet_heo_luoc_sup_1781720211.jpg'),
(@r18, @cat3, 'Ớt Sa Tế Nhà Làm Siêu Cay', 'Hũ sa tế dầu cay nồng kích thích vị giác', 10000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720215/food/ot_sa_te_nha_lam_sieu_cay_17'),
-- R19: Pizza Company
(@r19, @cat9, 'Pizza Hải Sản Nhiệt Đới (Size M)', 'Tôm, mực, nghêu phủ sốt Thousand Island và phô mai', 249000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720398/food/pizza_hai_san_nhiet_doi_size_m_1781720395.png'),
(@r19, @cat9, 'Pizza Gà Nướng Ba Chỉ Nấm', 'Thịt gà nướng, thịt heo xông khói trên nền sốt BBQ', 199000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720403/food/pizza_ga_nuong_ba_chi_nam_1781720401.webp'),
(@r19, @cat9, 'Mì Ý Sốt Bò Bằm Truyền Thống', 'Mì Spaghetti xào thịt bò bằm đậm sốt cà chua', 99000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720408/food/mi_y_sot_bo_bam_truyen_thong_1781720406.jpg'),
(@r19, @cat9, 'Cánh Gà Nướng BBQ (4 miếng)', 'Cánh gà tẩm ướp sốt mật ong ngọt cay đậm vị', 89000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720413/food/canh_ga_nuong_bbq_4_mieng_1781720411.jpg'),
(@r19, @cat9, 'Khoai Tây Múi Cau Chiên Giòn', 'Khoai tây bổ múi cau phủ bột gia vị thơm lừng', 49000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720419/food/khoai_tay_mui_cau_chien_gion_1781720416.png'),
(@r19, @cat9, 'Salad Hoàng Gia Sốt Caesar', 'Rau xà lách tươi xanh, thịt xông khói và bánh mì giòn', 69000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720422/food/salad_hoang_gia_sot_caesar_1781720422.webp'),

-- R20: Gà Rán KFC
(@r20, @cat4, 'Combo 2 Miếng Gà Rán Giòn Cay', 'Thương hiệu gà rán huyền thoại giòn tan rụm', 79000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720540/food/combo_2_mieng_ga_ran_gion_cay_1781720537.webp'),
(@r20, @cat4, 'Gà Popcorn Lắc Phô Mai (Cỡ Vừa)', 'Viên gà phi-lê tròn xoe tẩm bột chiên lắc bột phô mai', 45000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720544/food/ga_popcorn_lac_pho_mai_co_vua_1781720543.webp'),
(@r20, @cat4, 'Cơm Gà Fillet Quay Tiêu', 'Cơm nóng ăn kèm ức gà phi-lê sốt tiêu đen đậm đà', 42000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720548/food/com_ga_fillet_quay_tieu_1781720547.jpg'),
(@r20, @cat4, 'Bánh Trứng Egg Tart (1 chiếc)', 'Vỏ bánh ngàn lớp giòn xốp, nhân kem trứng nướng mềm', 18000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720551/food/banh_trung_egg_tart_1_chiec_1781720551.png'),
(@r20, @cat4, 'Khoai Tây Nghiền Sốt Gravy', 'Khoai tây nghiền mịn màng đẫm sốt thịt hầm', 22000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720556/food/khoai_tay_nghien_sot_gravy_1781720554.png'),
(@r20, @cat4, 'Salad Bắp Cải Trộn (Coleslaw)', 'Bắp cải xắt nhỏ trộn sốt mayo chua ngọt giải ngấy', 22000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720561/food/salad_bap_cai_tron_coleslaw_1781720559.jpg'),

-- R21: Highlands Coffee
(@r21, @cat6, 'Phin Sữa Đá Cỡ Lớn', 'Cà phê phin đậm đà kết hợp sữa đặc chuẩn gu Việt', 39000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781720892/kham-pha-do-uong-do-an-gia-ca-trong-menu-highlands-coffee-202112092235010227_zdhvle.jpg'),
(@r21, @cat6, 'Trà Sen Vàng Thạch Củ Năng', 'Cốt trà lài thanh tao, hạt sen bùi bùi, thạch củ năng giòn', 55000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781720831/tra-sen-vang-cu-nang-highlands-coffee-1698985351581_taydmn.webp'),
(@r21, @cat6, 'Trà Thạch Đào Cao Cấp', 'Trà đào đậm vị cùng miếng đào ngâm dầy dặn', 55000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720723/food/tra_thach_dao_cao_cap_1781720721.jpg'),
(@r21, @cat6, 'Freeze Trà Xanh Kem Béo', 'Thức uống đá xay trà xanh Matcha Nhật Bản phủ kem sữa', 65000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781720728/food/freeze_tra_xanh_kem_beo_1781720726.jpg'),
(@r21, @cat6, 'Phandi Hạnh Nhân Êm Dịu', 'Cà phê phin phá cách kết hợp sữa hạnh nhân thơm ngon', 55000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781721103/PHINDI_HANH_NHAN_wsqtbf.jpg'),
(@r21, @cat6, 'Bánh Mì Que Thịt Bằm Paté', 'Bánh mì que đặc sản giòn tan nóng hổi', 19000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781721168/banh-mi-que-pate-highlands-coffee-1698981843611_fyuvaa.webp'),

-- R22: Cơm Gà Hải Nam - Hàn Thuyên
(@r22, @cat1, 'Cơm Gà Luộc Hải Nam Chặt Khúc', 'Cơm nấu bằng nước dùng gà béo ngậy kèm nước chấm gừng tỏi', 48000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781721247/food/com_ga_luoc_hai_nam_chat_khuc_1781721247.jpg'),
(@r22, @cat1, 'Cơm Gà Xối Mỡ Da Giòn', 'Đùi gà góc tư xối mỡ nóng da giòn rụm bên ngoài', 52000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781721250/food/com_ga_xoi_mo_da_gion_1781721250.jpg'),
(@r22, @cat1, 'Gỏi Gà Xé Phay Hành Tây', 'Thịt gà xé trộn rau răm, hành tây chua ngọt', 40000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781721253/food/goi_ga_xe_phay_hanh_tay_1781721253.jpg'),
(@r22, @cat1, 'Lòng Gà Xào Mướp Giá Đỗ', 'Đĩa lòng gà xào ăn kèm cực kỳ bắt cơm', 30000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781721255/food/long_ga_xao_muop_gia_do_1781721256.jpg'),
(@r22, @cat1, 'Canh Lá Giang Nấu Thịt Gà', 'Chén canh chua lá giang thanh nhiệt cơ thể', 15000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781721407/Canh-ga-la-giang_ohceen.png'),
(@r22, @cat1, 'Chén Cơm Nấu Nước Dùng Gà Thêm', 'Hạt cơm dẻo quánh, vàng óng ánh thơm lừng', 10000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781721453/comthemnho-4039_alrpje.png'),

-- R23: Quán Ăn Vặt Bé Bi
(@r23, @cat7, 'Cá Viên Chiên Nước Mắm Tỏi Phi', 'Mẹt cá viên, bò viên, tôm viên ngập sốt mắm kẹo', 45000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781721545/food/ca_vien_chien_nuoc_mam_toi_phi_1781721545.jpg'),
(@r23, @cat7, 'Bánh Tráng Trộn Lòng Đào Siêu To', 'Bánh tráng trộn muối tắc kèm 2 quả trứng lòng đào béo', 30000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781721550/food/banh_trang_tron_long_dao_sieu_to_1781721549.jpg'),
(@r23, @cat7, 'Khoai Tây Lắc Bột Phô Mai', 'Khoai tây cọng chiên giòn rụm màu vàng óng', 25000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781721707/339fb6465b170c1423e3c6b2eef15c3c_r5aite.jpg'),
(@r23, @cat7, 'Phô Mai Que Kéo Sợi (Dĩa 3 thanh)', 'Thanh phô mai dài tẩm bột chiên xù kéo sợi cực đã', 27000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781721561/food/pho_mai_que_keo_soi_dia_3_thanh_1781721559.png'),
(@r23, @cat7, 'Nem Nướng Nha Trang Cuốn Sẵn', 'Suất 3 cuốn nem nướng kèm bánh tráng giòn và tương chấm', 35000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781722336/nem-nuong-nha-trang_1_pvlvjm.jpg'),
(@r23, @cat7, 'Trà Chanh Tắc Khổng Lồ', 'Ly 700ml trà tắc siêu đập tan cơn khát ngày hè', 15000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781722029/a594465ca530826d90dfa14483a22cd7_imzy8c.jpg'),

-- R24: Dimsum Tiến Phát
(@r24, @cat10, 'Há Cảo Tôm Thủy Tinh (Xửng 3 cái)', 'Vỏ bánh trong suốt thấy rõ nhân tôm tươi đỏ hồng', 38000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781722679/images_eg3lnr.jpg'),
(@r24, @cat10, 'Xíu Mại Tôm Thịt Nấm Đông Cô', 'Viên xíu mại chắc thịt bọc trứng múc vàng ươm', 38000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781722449/food/xiu_mai_tom_thit_nam_dong_co_1781722448.jpg'),
(@r24, @cat10, 'Bánh Bao Kim Sa Trứng Muối', 'Xửng 2 bánh bao nhân kim sa tan chảy mặn ngọt', 35000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781722798/85bc66fa-9f73-4fc9-b794-373be951fe3a.png'),
(@r24, @cat10, 'Chân Gà Hấp Tàu Xì Triều Châu', 'Chân gà hầm rục thấm đẫm sốt tàu xì cay nhẹ', 42000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781722458/food/chan_ga_hap_tau_xi_trieu_chau_1781722456.jpg'),
(@r24, @cat10, 'Bánh Cuốn Nhân Tôm Tươi', 'Lớp bánh cuốn mỏng mướt chan nước tương Hồng Kông', 45000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781722464/food/banh_cuon_nhan_tom_tuoi_1781722461.jpg'),
(@r24, @cat10, 'Sườn Non Hấp Tỏi Đen', 'Sườn heo chặt nhỏ hấp chín tới mềm ngọt đậm đà', 42000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781722467/food/suon_non_hap_toi_den_1781722467.png'),

-- R25: Cá Viên Chiên Nước Mắm LHK
(@r25, @cat7, 'Combo Cá Viên Thập Cẩm Khổng Lồ', 'Đầy đủ cá, bò, tôm, hồ lô, đậu hũ phô mai, xúc xích', 99000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781722861/food/combo_ca_vien_thap_cam_khong_lo_1781722859.jpg'),
(@r25, @cat7, 'Hồ Lô Nướng Than Hoa (Suất 2 xiên)', 'Hồ lô thịt viên tròn nướng thơm nức mũi', 24000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781723189/ho_lo_nuong_01_19f0e86c6f_doqwmt.jpg'),
(@r25, @cat7, 'Đậu Hũ Hải Sản Sốt Cay', 'Đậu hũ vuông mềm béo ngậy chiên ráo dầu', 20000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781722872/food/dau_hu_hai_san_sot_cay_1781722870.jpg'),
(@r25, @cat7, 'Bò Viên Sốt Đen Đậm Vị', 'Bò viên loại ngon giòn dai đậm vị tiêu', 22000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781723050/bo_vien_nuong_sot_ca_1_ebca6c03cf_iccrx1.jpg'),
(@r25, @cat7, 'Tôm Viên Chiên Đậu Đũa', 'Sự kết hợp độc đáo giữa tôm bằm và đậu đũa xắt nhỏ', 22000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781722878/food/tom_vien_chien_dau_dua_1781722878.jpg'),
(@r25, @cat7, 'Đậu Hũ Nhồi Chả Cá Thác Lác', 'Đậu hũ chiên kẹp chả cá thác lác dai bùi dẻo quánh', 25000.00, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781722881/food/dau_hu_nhoi_cha_ca_thac_lac_1781722881.jpg');