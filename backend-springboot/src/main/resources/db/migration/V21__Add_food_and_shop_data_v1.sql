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
INSERT INTO foods (restaurant_id, category_id, name, description, price) VALUES
-- R1: Cơm Tấm Phúc Lộc Thọ
(@r1, @cat1, 'Cơm Sườn Nướng Mật Ong', 'Sườn cốt lết nướng mật ong thơm phức', 42000.00),
(@r1, @cat1, 'Cơm Sườn Bì Chả', 'Combo truyền thống đầy đủ năng lượng', 55000.00),
(@r1, @cat1, 'Cơm Ba Rọi Nướng Sả', 'Thịt ba rọi giòn bì đượm hương sả', 45000.00),
(@r1, @cat1, 'Cơm Đùi Gà Nướng', 'Đùi gà góc tư ướp ngũ vị hương', 50000.00),
(@r1, @cat1, 'Canh Khổ Qua Nhồi Thịt', 'Canh giải nhiệt ăn kèm cơm tấm', 15000.00),
(@r1, @cat1, 'Chả Trứng Thêm', 'Một bánh chả trứng chưng truyền thống', 10000.00),

-- R2: Bánh Mì Huỳnh Hoa
(@r2, @cat2, 'Bánh Mì Đặc Biệt', 'Ổ bánh mì ổ nặng đô đầy đủ các loại chả, pate, bơ', 65000.00),
(@r2, @cat2, 'Bánh Mì Chả Lụa Xá Xíu', 'Nhân thịt xá xíu đậm đà kết hợp chả lụa hảo hạng', 50000.00),
(@r2, @cat2, 'Bánh Mì Pate Bơ Siêu Béo', 'Dành riêng cho tín đồ mê sốt bơ và pate Huỳnh Hoa', 40000.00),
(@r2, @cat2, 'Ruốc Chà Bông Gà (Hộp 200g)', 'Chà bông gà nhà làm, dai ngon đậm vị', 60000.00),
(@r2, @cat2, 'Pate Thượng Hạng (Hộp 100g)', 'Hộp pate chuẩn vị làm nên thương hiệu', 45000.00),
(@r2, @cat2, 'Ổ Bánh Mì Không Giòn Rụm', 'Bánh mì đặc ruột nướng nóng giòn', 70000.00),

-- R3: Sushi Hokkaido Sachi
(@r3, @cat8, 'Sashimi Cá Hồi Tươi', '5 lát cá hồi tươi sống nhập khẩu từ Na-uy', 165000.00),
(@r3, @cat8, 'Combo Sushi Mẫu', 'Tổng hợp sushi tôm, cá hồi, trứng cá chuồn', 220000.00),
(@r3, @cat8, 'Mì Udon Hải Sản Hotpot', 'Mì udon sợi dày nấu cùng tôm, mực, nghêu', 145000.00),
(@r3, @cat8, 'Cơm Lươn Nhật Bản (Unadon)', 'Lươn nướng sốt Kabayaki đậm đà phủ trên cơm', 280000.00),
(@r3, @cat8, 'Tempura Tôm Sú', '3 con tôm sú chiên xù kiểu Nhật giòn rụm', 95000.00),
(@r3, @cat8, 'Súp Miso Truyền Thống', 'Súp rong biển nấu cùng đậu hũ non thanh mát', 30000.00),

-- R4: Bún Mọc 111 Ngô Thì Nhậm
(@r4, @cat3, 'Bún Mọc Sườn Non', 'Sườn heo ninh nhừ kết hợp mọc tươi dai giòn', 55000.00),
(@r4, @cat3, 'Bún Mọc Đặc Biệt Ngô Thì Nhậm', 'Đầy đủ mọc chiên, mọc hấp, giò tai và sườn', 65000.00),
(@r4, @cat3, 'Bún Mọc Dọc Mùng', 'Nước dùng thanh ngọt ăn kèm dọc mùng giòn sần sật', 50000.00),
(@r4, @cat3, 'Mọc Hấp Ăn Thêm (Chén 3 viên)', 'Viên mọc hấp truyền thống thơm mùi tiêu', 15000.00),
(@r4, @cat3, 'Mọc Chiên Vàng (Chén 3 viên)', 'Mọc chiên thơm béo ngậy cực cuốn', 15000.00),
(@r4, @cat3, 'Chả Lụa Gói Lá Chuối', 'Cây chả lụa ăn kèm tăng thêm hương vị', 12000.00),

-- R5: Lẩu Haidilao
(@r5, @cat10, 'Nước Cốt Lẩu Súp Cay Tứ Xuyên', 'Hương vị cay nồng chuẩn vị Haidilao', 90000.00),
(@r5, @cat10, 'Nước Cốt Lẩu Nấm Bổ Dưỡng', 'Thơm mùi nấm đông cô và thảo mộc thanh đạm', 80000.00),
(@r5, @cat10, 'Thịt Bò Haidilao Thượng Hạng', 'Thịt bò tươi thái lát mỏng mềm tan', 185000.00),
(@r5, @cat10, 'Đậu Hũ Phô Mai Chiên', 'Hộp 6 viên đậu hũ béo ngậy tan chảy', 65000.00),
(@r5, @cat10, 'Mì Tươi Biểu Diễn Haidilao', 'Suất mì tươi làm thủ công tại bàn', 40000.00),
(@r5, @cat10, 'Vò Viên Tôm Hoàng Kim', 'Tôm tươi xay nhuyễn bọc trứng muối siêu ngon', 120000.00),

-- R6: Phở Thìn Hà Nội
(@r6, @cat3, 'Phở Tái Lăn Truyền Thống', 'Thịt bò xào tái lăn nhanh với tỏi và hành lá ngập tràn', 65000.00),
(@r6, @cat3, 'Phở Nạm Gầu Giòn', 'Sự kết hợp hoàn hảo giữa nạm mềm và gầu giòn', 60000.00),
(@r6, @cat3, 'Phở Gà Ta Xé Phay', 'Thịt gà ta da giòn, nước dùng thanh ngọt', 55000.00),
(@r6, @cat3, 'Quẩy Nóng Giòn', 'Đĩa 3 chiếc quẩy ăn kèm phở chuẩn vị Bắc', 10000.00),
(@r6, @cat3, 'Trứng Chần Sốt Nước Tiết', 'Chén trứng chần bổ dưỡng cho bữa sáng', 12000.00),
(@r6, @cat3, 'Phở Đặc Biệt (Tái, Nạm, Gầu, Trứng)', 'Tô phở khổng lồ full topping nâng cấp', 85000.00),

-- R7: Hủ Tiếu Nam Vang Thành Đạt
(@r7, @cat3, 'Hủ Tiếu Khô Đặc Biệt', 'Hủ tiếu trộn sốt hắc xì dầu kèm chén súp sườn tinh túy', 55000.00),
(@r7, @cat3, 'Hủ Tiếu Nước Đầy Đủ', 'Nước dùng ngọt xương ống, tôm, mực, trứng cút', 50000.00),
(@r7, @cat3, 'Hủ Tiếu Mì Sườn Kho', 'Sợi mì trứng dai mềm hầm cùng sườn heo bản lớn', 60000.00),
(@r7, @cat3, 'Hủ Tiếu Mực Tươi Khô', 'Mực ống tươi roi rói trụng vừa chín tới', 55000.00),
(@r7, @cat3, 'Súp Hoành Thánh Tôm Thịt', 'Hoành thánh gói tay ngập nhân thịt tôm bằm', 35000.00),
(@r7, @cat3, 'Xí Quách Heo Thêm', 'Khúc xương ống tủy lớn kèm nước lèo', 30000.00),

-- R8: Bún Chả Hà Nội 1986
(@r8, @cat3, 'Mẹt Bún Chả Đặc Biệt', 'Gồm chả băm, chả miếng nướng than hoa, bún và rau sống', 55000.00),
(@r8, @cat3, 'Bún Chả Nem Cua Bể', 'Combo gồm bún chả truyền thống và 1 chiếc nem cua bể', 75000.00),
(@r8, @cat3, 'Nem Cua Bể Giòn Rụm (1 chiếc)', 'Nem vuông nhân thịt ghẹ và cua biển đậm vị', 25000.00),
(@r8, @cat3, 'Thịt Ba Chỉ Nướng Thêm', 'Suất thịt nướng cháy cạnh ăn thêm', 25000.00),
(@r8, @cat3, 'Chả Băm Cuốn Lá Lốt Thêm', 'Suất 3 viên chả băm thơm lừng', 20000.00),
(@r8, @cat3, 'Nước Sấu Đá Hà Nội', 'Thức uống giải nhiệt chua ngọt thanh mát', 15000.00),

-- R9: Bánh Xèo Ăn Là Nghiền
(@r9, @cat7, 'Bánh Xèo Tôm Nhảy Đất Đỏ', 'Nhân tôm đất tươi rói, giá đỗ, thịt ba rọi', 70000.00),
(@r9, @cat7, 'Bánh Xèo Nấm Đùi Gà Đồ Chay', 'Lựa chọn thanh đạm với nấm và đậu xanh', 60000.00),
(@r9, @cat7, 'Bánh Khọt Vũng Tàu (Đĩa 8 cái)', 'Bánh khọt tôm béo ngậy nước cốt dừa', 55000.00),
(@r9, @cat7, 'Rau Rừng Tây Ninh Ăn Kèm', 'Mẹt rau rừng đủ loại cuốn bánh xèo', 15000.00),
(@r9, @cat7, 'Bánh Tráng Phơi Sương Cuốn Thêm', 'Xấp bánh tráng dẻo dai cao cấp', 10000.00),
(@r9, @cat7, 'Nước Mắm Chua Ngọt Bí Truyền', 'Chai nước mắm pha sẵn chuẩn vị', 10000.00),

-- R10: Gà Rán Popeyes
(@r10, @cat4, '2 Miếng Gà Giòn Sả Ớt', 'Gà rán da giòn đậm đà hương vị sả ớt Việt Nam', 76000.00),
(@r10, @cat4, '3 Miếng Gà Không Xương (Tenders)', 'Ức gà tẩm bột chiên phi-lê dễ ăn', 62000.00),
(@r10, @cat4, 'Burger Gà Phi-lê Thượng Hạng', 'Burger nhân gà giòn, sốt mayo và xà lách', 49000.00),
(@r10, @cat4, 'Khoai Tây Chiên Cỡ Lớn', 'Khoai tây tẩm gia vị Cajun độc quyền', 35000.00),
(@r10, @cat4, 'Bánh Quy Mật Ong (Biscuit)', 'Bánh nướng bơ sữa phủ mật ong ngọt dịu', 15000.00),
(@r10, @cat4, 'Ly Pepsi Tươi Mát Lạnh', 'Nước ngọt giải khát ăn kèm gà rán', 18000.00),

-- R11: Trà Sữa Koi Thé
(@r11, @cat5, 'Trà Sữa Trân Châu Hoàng Kim', 'Trà sữa signature kết hợp trân châu hoàng kim dai giòn', 65000.00),
(@r11, @cat5, 'Lục Trà Macchiato', 'Trà xanh thanh mát phủ lớp kem sữa mặn béo ngậy', 60000.00),
(@r11, @cat5, 'Ovaltine Macchiato', 'Thức uống sô-cô-la lúa mạch kết hợp kem béo', 65000.00),
(@r11, @cat5, 'Trà Sữa Sương Sáo Cắt Nhỏ', 'Sương sáo thanh mát hòa quyện cốt trà sữa', 58000.00),
(@r11, @cat5, 'Trà Xanh Sữa Konjac', 'Hương vị trà xanh nhài nhẹ nhàng cùng thạch giòn', 60000.00),
(@r11, @cat5, 'Trà Đen Cốt Macchiato', 'Hương vị trà đen đậm đà kèm kem sữa béo', 60000.00),

-- R12: Bún Đậu Mắm Tôm A Chảnh
(@r12, @cat3, 'Mẹt Bún Đậu Đầy Đủ', 'Đậu hũ chiên giòn, thịt luộc, chả cốm, nem chua rán, dồi chiên', 65000.00),
(@r12, @cat3, 'Mẹt Bún Đậu Chả Cốm Phổ Thông', 'Dành cho người ăn đơn giản với đậu và chả cốm', 45000.00),
(@r12, @cat3, 'Nem Chua Rán Hà Nội (Đĩa 4 chiếc)', 'Nem chua tẩm bột chiên xù ráo dầu', 30000.00),
(@r12, @cat3, 'Dồi Sụn Nướng Thơm', 'Dồi heo nhân sụn sần sật nướng vàng', 35000.00),
(@r12, @cat3, 'Chả Cốm Chiên Nóng Thêm', '1 khoanh chả cốm dẻo thơm nồng nàn', 15000.00),
(@r12, @cat3, 'Nước Mơ Ngâm Đường Phố Cổ', 'Nước giải khát vị chua mặn ngọt hài hòa', 15000.00),

-- R13: Tokbokki Saigon
(@r13, @cat8, 'Lẩu Tokbokki Truyền Thống', 'Nồi lẩu gồm bánh gạo, chả cá Hàn Quốc, mì ramen và sốt cay', 139000.00),
(@r13, @cat8, 'Tokbokki Phô Mai Kéo Sợi', 'Bánh gạo xào sốt cay phủ ngập phô mai Mozzarella', 69000.00),
(@r13, @cat8, 'Gà Rán Sốt Gia Vị Sweet & Spicy', 'Gà rút xương chiên giòn đẫm sốt ngọt cay', 89000.00),
(@r13, @cat8, 'Miến Trộn Hàn Quốc Japchae', 'Miến dai trộn dầu mè, rau củ và thịt bò thái sợi', 59000.00),
(@r13, @cat8, 'Kimbap Truyền Thống', 'Cơm cuộn rong biển nhân trứng, xúc xích, dưa leo', 39000.00),
(@r13, @cat8, 'Chả Cá Xiên Nước Súp (3 xiên)', 'Chả cá Odeng ngập trong nước dùng thanh thanh', 30000.00),

-- R14: Chè Thái Ý Phương
(@r14, @cat7, 'Chè Thái Đặc Biệt Siêu Sầu Riêng', 'Chè trái cây cốt dừa kèm múi sầu riêng tươi nguyên chất', 45000.00),
(@r14, @cat7, 'Chè Khúc Bạch Truyền Thống', 'Khúc bạch bơ sữa, hạnh nhân lát và quả vải ngọt', 35000.00),
(@r14, @cat7, 'Sữa Chua Trái Cây Tô', 'Sữa chua nhà làm mít, xoài, bơ, hạt đác', 40000.00),
(@r14, @cat7, 'Bánh Flan Nước Cốt Dừa (Cặp 2 cái)', 'Bánh flan mềm mịn đắng nhẹ vị cà phê', 24000.00),
(@r14, @cat7, 'Gỏi Cuốn Tôm Thịt (Dĩa 3 cuốn)', 'Đồ ăn kèm siêu hot tại quán ăn cùng sốt tương đậu', 30000.00),
(@r14, @cat7, 'Bánh Tráng Trộn Sợi Tây Ninh', 'Bánh tráng trộn bò khô, mực xé, trứng cút', 25000.00),

-- R15: Súp Cua Hạnh
(@r15, @cat7, 'Súp Cua Trứng Bắc Thảo', 'Súp cua đặc sánh kèm nguyên quả trứng bách thảo bùi bùi', 35000.00),
(@r15, @cat7, 'Súp Cua Bong Bóng Cá Cao Cấp', 'Súp nấu cùng thịt cua tuyết và bong bóng cá dầy dặn', 45000.00),
(@r15, @cat7, 'Súp Cua Óc Heo Trứng Cút', 'Óc heo tươi làm sạch chưng cách thủy bổ dưỡng', 40000.00),
(@r15, @cat7, 'Chén Tủy Heo Chưng Thêm', 'Tủy béo ngậy ăn kèm súp tăng vị đậm đà', 20000.00),
(@r15, @cat7, 'Hột Vịt Bắc Thảo Thêm', '1 quả trứng bách thảo ăn thêm', 10000.00),
(@r15, @cat7, 'Cồi Sò Điệp Trụng Súp', '3 cồi sò điệp tươi ngon giòn ngọt', 25000.00),

-- R16: Cơm Tấm Long Xuyên
(@r16, @cat1, 'Cơm Tấm Nhuyễn Thịt Kho Trứng Kho', 'Cơm tấm nhuyễn hạt nhỏ, thịt kho xắt hạt lựu đặc trưng', 40000.00),
(@r16, @cat1, 'Cơm Tấm Bì Chả Miền Tây', 'Hương vị chả chưng miền Tây béo bùi', 35000.00),
(@r16, @cat1, 'Thịt Kho Cắt Nhỏ Gọi Thêm', 'Suất thịt kho băm nhỏ ăn kèm nước sốt kẹo', 15000.00),
(@r16, @cat1, 'Trứng Vịt Kho Lòng Đào', 'Trứng kho thấm vị nước dừa tươi', 80000.00),
(@r16, @cat1, 'Canh Cải Ngọt Thịt Bằm', 'Canh ăn kèm cơm tấm nóng hổi', 12000.00),
(@r16, @cat1, 'Dưa Đu Đủ Ngâm Chua Ngọt', 'Đồ chua ăn kèm chống ngán chuẩn vị miền Tây', 5000.00),

-- R17: The Coffee House
(@r17, @cat6, 'Cà Phê Sữa Đá Nhà Làm', 'Đậm đà hạt cà phê Robusta rang xay nguyên chất', 39000.00),
(@r17, @cat6, 'Trà Đào Đá Xay Phúc Bồn Tử', 'Trà đào xay mát lạnh hòa quyện mứt phúc bồn tử', 59000.00),
(@r17, @cat6, 'Trà Thạch Vải Cao Cấp', 'Cốt trà lài thanh mát cùng hạt vải ngâm giòn', 55000.00),
(@r17, @cat6, 'Frosty Sô-cô-la Đá Xay', 'Thức uống đá xay đậm vị hạt cacao nguyên chất', 65000.00),
(@r17, @cat6, 'Bánh Mì Que Paris', 'Bánh mì que nhân pate gan nướng giòn', 19000.00),
(@r17, @cat6, 'Bánh Mousse Gấu Socola', 'Bánh ngọt hình chú gấu dễ thương, cốt bánh mềm mại', 39000.00),

-- R18: Bún Bò Huế Đông Ba
(@r18, @cat3, 'Bún Bò Tái Nạm Bản Lớn', 'Bắp bò tái tươi hoa kết hợp nạm bò ninh mềm', 55000.00),
(@r18, @cat3, 'Bún Bò Gân Chả Cua', 'Gân bò dai sần sật và viên chả cua xứ Huế chính gốc', 60000.00),
(@r18, @cat3, 'Bún Bò Huế Đặc Biệt Giò Heo', 'Tô đầy đủ có khoanh giò heo nạc lớn đại ca', 75000.00),
(@r18, @cat3, 'Chả Cua Huế Gọi Thêm (2 viên)', 'Viên chả cua quét tay dai ngon thơm ngọt', 16000.00),
(@r18, @cat3, 'Chén Tiết Heo Luộc Súp', 'Tiết heo mềm mịn như thạch không bị xốp', 8000.00),
(@r18, @cat3, 'Ớt Sa Tế Nhà Làm Siêu Cay', 'Hũ sa tế dầu cay nồng kích thích vị giác', 10000.00),

-- R19: Pizza Company
(@r19, @cat9, 'Pizza Hải Sản Nhiệt Đới (Size M)', 'Tôm, mực, nghêu phủ sốt Thousand Island và phô mai', 249000.00),
(@r19, @cat9, 'Pizza Gà Nướng Ba Chỉ Nấm', 'Thịt gà nướng, thịt heo xông khói trên nền sốt BBQ', 199000.00),
(@r19, @cat9, 'Mì Ý Sốt Bò Bằm Truyền Thống', 'Mì Spaghetti xào thịt bò bằm đậm sốt cà chua', 99000.00),
(@r19, @cat9, 'Cánh Gà Nướng BBQ (4 miếng)', 'Cánh gà tẩm ướp sốt mật ong ngọt cay đậm vị', 89000.00),
(@r19, @cat9, 'Khoai Tây Múi Cau Chiên Giòn', 'Khoai tây bổ múi cau phủ bột gia vị thơm lừng', 49000.00),
(@r19, @cat9, 'Salad Hoàng Gia Sốt Caesar', 'Rau xà lách tươi xanh, thịt xông khói và bánh mì giòn', 69000.00),

-- R20: Gà Rán KFC
(@r20, @cat4, 'Combo 2 Miếng Gà Rán Giòn Cay', 'Thương hiệu gà rán huyền thoại giòn tan rụm', 79000.00),
(@r20, @cat4, 'Gà Popcorn Lắc Phô Mai (Cỡ Vừa)', 'Viên gà phi-lê tròn xoe tẩm bột chiên lắc bột phô mai', 45000.00),
(@r20, @cat4, 'Cơm Gà Fillet Quay Tiêu', 'Cơm nóng ăn kèm ức gà phi-lê sốt tiêu đen đậm đà', 42000.00),
(@r20, @cat4, 'Bánh Trứng Egg Tart (1 chiếc)', 'Vỏ bánh ngàn lớp giòn xốp, nhân kem trứng nướng mềm', 18000.00),
(@r20, @cat4, 'Khoai Tây Nghiền Sốt Gravy', 'Khoai tây nghiền mịn màng đẫm sốt thịt hầm', 22000.00),
(@r20, @cat4, 'Salad Bắp Cải Trộn (Coleslaw)', 'Bắp cải xắt nhỏ trộn sốt mayo chua ngọt giải ngấy', 22000.00),

-- R21: Highlands Coffee
(@r21, @cat6, 'Phin Sữa Đá Cỡ Lớn', 'Cà phê phin đậm đà kết hợp sữa đặc chuẩn gu Việt', 39000.00),
(@r21, @cat6, 'Trà Sen Vàng Thạch Củ Năng', 'Cốt trà lài thanh tao, hạt sen bùi bùi, thạch củ năng giòn', 55000.00),
(@r21, @cat6, 'Trà Thạch Đào Cao Cấp', 'Trà đào đậm vị cùng miếng đào ngâm dầy dặn', 55000.00),
(@r21, @cat6, 'Freeze Trà Xanh Kem Béo', 'Thức uống đá xay trà xanh Matcha Nhật Bản phủ kem sữa', 65000.00),
(@r21, @cat6, 'Phandi Hạnh Nhân Êm Dịu', 'Cà phê phin phá cách kết hợp sữa hạnh nhân thơm ngon', 55000.00),
(@r21, @cat6, 'Bánh Mì Que Thịt Bằm Paté', 'Bánh mì que đặc sản giòn tan nóng hổi', 19000.00),

-- R22: Cơm Gà Hải Nam - Hàn Thuyên
(@r22, @cat1, 'Cơm Gà Luộc Hải Nam Chặt Khúc', 'Cơm nấu bằng nước dùng gà béo ngậy kèm nước chấm gừng tỏi', 48000.00),
(@r22, @cat1, 'Cơm Gà Xối Mỡ Da Giòn', 'Đùi gà góc tư xối mỡ nóng da giòn rụm bên ngoài', 52000.00),
(@r22, @cat1, 'Gỏi Gà Xé Phay Hành Tây', 'Thịt gà xé trộn rau răm, hành tây chua ngọt', 40000.00),
(@r22, @cat1, 'Lòng Gà Xào Mướp Giá Đỗ', 'Đĩa lòng gà xào ăn kèm cực kỳ bắt cơm', 30000.00),
(@r22, @cat1, 'Canh Lá Giang Nấu Thịt Gà', 'Chén canh chua lá giang thanh nhiệt cơ thể', 15000.00),
(@r22, @cat1, 'Chén Cơm Nấu Nước Dùng Gà Thêm', 'Hạt cơm dẻo quánh, vàng óng ánh thơm lừng', 10000.00),

-- R23: Quán Ăn Vặt Bé Bi
(@r23, @cat7, 'Cá Viên Chiên Nước Mắm Tỏi Phi', 'Mẹt cá viên, bò viên, tôm viên ngập sốt mắm kẹo', 45000.00),
(@r23, @cat7, 'Bánh Tráng Trộn Lòng Đào Siêu To', 'Bánh tráng trộn muối tắc kèm 2 quả trứng lòng đào béo', 30000.00),
(@r23, @cat7, 'Khoai Tây Lắc Bột Phô Mai', 'Khoai tây cọng chiên giòn rụm màu vàng óng', 25000.00),
(@r23, @cat7, 'Phô Mai Que Kéo Sợi (Dĩa 3 thanh)', 'Thanh phô mai dài tẩm bột chiên xù kéo sợi cực đã', 27000.00),
(@r23, @cat7, 'Nem Nướng Nha Trang Cuốn Sẵn', 'Suất 3 cuốn nem nướng kèm bánh tráng giòn và tương chấm', 35000.00),
(@r23, @cat7, 'Trà Chanh Tắc Khổng Lồ', 'Ly 700ml trà tắc siêu đập tan cơn khát ngày hè', 15000.00),

-- R24: Dimsum Tiến Phát
(@r24, @cat10, 'Há Cảo Tôm Thủy Tinh (Xửng 3 cái)', 'Vỏ bánh trong suốt thấy rõ nhân tôm tươi đỏ hồng', 38000.00),
(@r24, @cat10, 'Xíu Mại Tôm Thịt Nấm Đông Cô', 'Viên xíu mại chắc thịt bọc trứng múc vàng ươm', 38000.00),
(@r24, @cat10, 'Bánh Bao Kim Sa Trứng Muối', 'Xửng 2 bánh bao nhân kim sa tan chảy mặn ngọt', 35000.00),
(@r24, @cat10, 'Chân Gà Hấp Tàu Xì Triều Châu', 'Chân gà hầm rục thấm đẫm sốt tàu xì cay nhẹ', 42000.00),
(@r24, @cat10, 'Bánh Cuốn Nhân Tôm Tươi', 'Lớp bánh cuốn mỏng mướt chan nước tương Hồng Kông', 45000.00),
(@r24, @cat10, 'Sườn Non Hấp Tỏi Đen', 'Sườn heo chặt nhỏ hấp chín tới mềm ngọt đậm đà', 42000.00),

-- R25: Cá Viên Chiên Nước Mắm LHK
(@r25, @cat7, 'Combo Cá Viên Thập Cẩm Khổng Lồ', 'Đầy đủ cá, bò, tôm, hồ lô, đậu hũ phô mai, xúc xích', 99000.00),
(@r25, @cat7, 'Hồ Lô Nướng Than Hoa (Suất 2 xiên)', 'Hồ lô thịt viên tròn nướng thơm nức mũi', 24000.00),
(@r25, @cat7, 'Đậu Hũ Hải Sản Sốt Cay', 'Đậu hũ vuông mềm béo ngậy chiên ráo dầu', 20000.00),
(@r25, @cat7, 'Bò Viên Sốt Đen Đậm Vị', 'Bò viên loại ngon giòn dai đậm vị tiêu', 22000.00),
(@r25, @cat7, 'Tôm Viên Chiên Đậu Đũa', 'Sự kết hợp độc đáo giữa tôm bằm và đậu đũa xắt nhỏ', 22000.00),
(@r25, @cat7, 'Đậu Hũ Nhồi Chả Cá Thác Lác', 'Đậu hũ chiên kẹp chả cá thác lác dai bùi dẻo quánh', 25000.00);