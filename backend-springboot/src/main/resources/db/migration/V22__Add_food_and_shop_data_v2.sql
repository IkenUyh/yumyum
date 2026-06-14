-- V17__Insert_more_mock_restaurants_and_foods.sql

-- =============================================================================
-- 1. TRUY VẤN LẠI ID DANH MỤC CŨ VÀO BIẾN TẠM (VÌ CHẠY TRÊN SESSION MỚI)
-- =============================================================================
SET @cat1 = (SELECT id FROM categories WHERE name = 'Cơm' LIMIT 1);
SET @cat2 = (SELECT id FROM categories WHERE name = 'Bánh Mì' LIMIT 1);
SET @cat3 = (SELECT id FROM categories WHERE name = 'Phở & Bún' LIMIT 1);
SET @cat4 = (SELECT id FROM categories WHERE name = 'Gà Rán & Fastfood' LIMIT 1);
SET @cat5 = (SELECT id FROM categories WHERE name = 'Trà Sữa' LIMIT 1);
SET @cat6 = (SELECT id FROM categories WHERE name = 'Cà Phê & Đồ Uống' LIMIT 1);
SET @cat7 = (SELECT id FROM categories WHERE name = 'Ăn Vặt' LIMIT 1);
SET @cat8 = (SELECT id FROM categories WHERE name = 'Món Nhật & Hàn' LIMIT 1);
SET @cat9 = (SELECT id FROM categories WHERE name = 'Ý & Pizza' LIMIT 1);
SET @cat10 = (SELECT id FROM categories WHERE name = 'Lẩu & Dimsum' LIMIT 1);

-- =============================================================================
-- 2. TẠO THÊM TÀI KHOẢN CHỦ CỬA HÀNG MỚI (5 MERCHANTS)
-- =============================================================================
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0911000001', 'password123', 'Lý Đại Phát - Merchant Chợ Lớn', 'MERCHANT', TRUE);
SET @m6 = LAST_INSERT_ID();
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0911000002', 'password123', 'Vũ Nam Phương - Merchant Phú Mỹ Hưng', 'MERCHANT', TRUE);
SET @m7 = LAST_INSERT_ID();
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0911000003', 'password123', 'Đặng Kim Sơn - Merchant Tân Bình', 'MERCHANT', TRUE);
SET @m8 = LAST_INSERT_ID();
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0911000004', 'password123', 'Bùi Ngọc Diệp - Merchant Gò Vấp', 'MERCHANT', TRUE);
SET @m9 = LAST_INSERT_ID();
INSERT INTO users (phone_number, password, full_name, role, is_active) VALUES ('0911000005', 'password123', 'Trịnh Gia Bảo - Merchant Thủ Đức', 'MERCHANT', TRUE);
SET @m10 = LAST_INSERT_ID();

-- =============================================================================
-- 3. TẠO 40 CỬA HÀNG MỚI (TỪ R26 ĐẾN R65)
-- Phân bổ tọa độ thực tế tại khu vực Q5, Q7, Tân Bình, Gò Vấp, Thủ Đức TP.HCM
-- =============================================================================

-- Nhóm Q5 - Chợ Lớn (@m6 quản lý)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m6, 'Sủi Cảo Đại Lộ - Hà Tôn Quyền', 'Hà Tôn Quyền, Quận 5', '14:00:00', '23:00:00', 10.7562, 106.6575, 40); SET @r26 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m6, 'Chè Tàu Hà Ký', 'Nguyễn Trãi, Quận 5', '10:00:00', '22:30:00', 10.7514, 106.6621, 30); SET @r27 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m6, 'Cơm Gà Đông Nguyên', 'Nguyễn Trãi, Quận 5', '09:00:00', '21:00:00', 10.7525, 106.6612, 35); SET @r28 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m6, 'Hủ Tiếu Mì Hoàng Gia', 'Trần Hưng Đạo, Quận 5', '06:00:00', '22:00:00', 10.7541, 106.6710, 20); SET @r29 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m6, 'Lẩu Đầu Cá Dân Ích', 'Châu Văn Liêm, Quận 5', '15:00:00', '22:00:00', 10.7512, 106.6548, 25); SET @r30 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m6, 'Bánh Bao Thọ Phát - Nguyễn Tri Phương', 'Nguyễn Tri Phương, Quận 5', '05:00:00', '23:30:00', 10.7569, 106.6669, 60); SET @r31 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m6, 'Phá Lấu Bò Cô Thảo - Q5', 'Nguyễn Trãi, Quận 5', '13:00:00', '22:00:00', 10.7533, 106.6601, 30); SET @r32 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m6, 'Vịt Quay Vĩnh Phong', 'Bùi Hữu Nghĩa, Quận 5', '06:00:00', '20:00:00', 10.7552, 106.6742, 45); SET @r33 = LAST_INSERT_ID();

-- Nhóm Q7 - Phú Mỹ Hưng (@m7 quản lý)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m7, 'Pizza 4Ps - Phú Mỹ Hưng', 'Hoàng Văn Thái, Quận 7', '11:00:00', '22:00:00', 10.7292, 106.7194, 40); SET @r34 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m7, 'Gà Rán Popeyes - Nguyễn Thị Thập', 'Nguyễn Thị Thập, Quận 7', '09:30:00', '22:00:00', 10.7412, 106.7025, 30); SET @r35 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m7, 'Đậu Homemade - Nguyễn Văn Linh', 'Nguyễn Văn Linh, Quận 7', '10:00:00', '21:45:00', 10.7315, 106.7118, 35); SET @r36 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m7, 'El Gaucho Argentinian Steakhouse', 'Tôn Dật Tiên, Quận 7', '16:00:00', '23:00:00', 10.7281, 106.7189, 15); SET @r37 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m7, 'Tokyo Deli - Nguyễn Đức Cảnh', 'Nguyễn Đức Cảnh, Quận 7', '11:00:00', '22:00:00', 10.7264, 106.7082, 25); SET @r38 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m7, 'Gong Cha - Nguyễn Thị Thập', 'Nguyễn Thị Thập, Quận 7', '08:30:00', '22:00:00', 10.7405, 106.7011, 40); SET @r39 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m7, 'Phở Hùng - Nguyễn Văn Linh', 'Nguyễn Văn Linh, Quận 7', '06:00:00', '22:00:00', 10.7328, 106.7125, 30); SET @r40 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m7, 'Bánh Mì Minh Nhật - Quận 7', 'Huỳnh Tấn Phát, Quận 7', '06:00:00', '21:00:00', 10.7452, 106.7348, 25); SET @r41 = LAST_INSERT_ID();

-- Nhóm Tân Bình / Phú Nhuận (@m8 quản lý)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m8, 'Cơm Niêu Sài Gòn - Trường Sơn', 'Trường Sơn, Tân Bình', '10:00:00', '22:00:00', 10.8018, 106.6652, 30); SET @r42 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m8, 'Bún Chả Sinh Từ - Bạch Đằng', 'Bạch Đằng, Tân Bình', '08:00:00', '21:30:00', 10.8089, 106.6714, 35); SET @r43 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m8, 'Bún Nước Quyên - Phú Nhuận', 'Vũ Huy Tấn, Phú Nhuận', '15:00:00', '23:00:00', 10.7942, 106.6912, 40); SET @r44 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m8, 'Trà Sữa Phúc Long - Phổ Quang', 'Phổ Quang, Tân Bình', '07:00:00', '22:30:00', 10.8025, 106.6618, 50); SET @r45 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m8, 'Gà Nướng Ò Ó O - Nguyễn Hồng Đào', 'Nguyễn Hồng Đào, Tân Bình', '10:00:00', '21:45:00', 10.7964, 106.6435, 25); SET @r46 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m8, 'Bánh Canh Cua Út Lệ - Bàu Cát', 'Bàu Cát, Tân Bình', '15:30:00', '22:00:00', 10.7925, 106.6412, 35); SET @r47 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m8, 'Nem Nướng Đà Lạt Gánh - Út Tịch', 'Út Tịch, Tân Bình', '09:00:00', '21:30:00', 10.7981, 106.6602, 20); SET @r48 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m8, 'Trung Nguyên Legend - Cộng Hòa', 'Cộng Hòa, Tân Bình', '06:30:00', '22:00:00', 10.8011, 106.6489, 30); SET @r49 = LAST_INSERT_ID();

-- Nhóm Gò Vấp (@m9 quản lý)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m9, 'Lẩu Gà Lá É Tao Ngộ - Quang Trung', 'Quang Trung, Gò Vấp', '15:00:00', '23:30:00', 10.8342, 106.6618, 30); SET @r50 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m9, 'Bún Đậu Mạc Văn Khoa - Phan Văn Trị', 'Phan Văn Trị, Gò Vấp', '10:00:00', '22:00:00', 10.8285, 106.6892, 45); SET @r51 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m9, 'Bò Né Lệ Hồng - Nguyễn Oanh', 'Nguyễn Oanh, Gò Vấp', '06:00:00', '11:00:00', 10.8315, 106.6754, 40); SET @r52 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m9, 'Mì Quảng Sông Trà - Lê Đức Thọ', 'Lê Đức Thọ, Gò Vấp', '06:00:00', '21:30:00', 10.8441, 106.6712, 25); SET @r53 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m9, 'Bánh Tráng Nướng Đà Lạt - Thống Nhất', 'Thống Nhất, Gò Vấp', '16:00:00', '22:30:00', 10.8321, 106.6795, 30); SET @r54 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m9, 'Ốc Khánh - Nguyễn Văn Lượng', 'Nguyễn Văn Lượng, Gò Vấp', '15:00:00', '23:00:00', 10.8365, 106.6738, 35); SET @r55 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m9, 'Ốc Như - Phạm Văn Đồng', 'Phạm Văn Đồng, Gò Vấp', '15:00:00', '23:00:00', 10.8252, 106.6912, 40); SET @r56 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m9, 'Bún Mắm Miền Tây - Lê Quang Định', 'Lê Quang Định, Gò Vấp', '09:00:00', '21:00:00', 10.8192, 106.6901, 20); SET @r57 = LAST_INSERT_ID();

-- Nhóm Thủ Đức (@m10 quản lý)
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m10, 'Cơm Tấm Ngô Quyền - Thủ Đức', 'Dân Chủ, Thủ Đức', '06:00:00', '14:00:00', 10.8482, 106.7612, 40); SET @r58 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m10, 'Bún Mọc 111 Ngô Thì Nhậm - Kha Vạn Cân', 'Kha Vạn Cân, Thủ Đức', '06:30:00', '21:00:00', 10.8521, 106.7542, 30); SET @r59 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m10, 'Cơm Tấm Long Xuyên - Đặng Văn Bi', 'Đặng Văn Bi, Thủ Đức', '06:00:00', '20:30:00', 10.8461, 106.7675, 30); SET @r60 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m10, 'Gà Rán Jollibee - Võ Văn Ngân', 'Võ Văn Ngân, Thủ Đức', '09:00:00', '22:00:00', 10.8499, 106.7554, 45); SET @r61 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m10, 'Mixue - Làng Đại Học', 'Đường nội bộ ĐHQG, Thủ Đức', '08:30:00', '23:00:00', 10.8732, 106.8024, 60); SET @r62 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m10, 'Trà Sữa Tocotoco - Lê Văn Việt', 'Lê Văn Việt, Quận 9 cũ', '09:00:00', '22:00:00', 10.8468, 106.7812, 40); SET @r63 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m10, 'Bún Bò Huế Ngự Uyển - Thủ Đức', 'Tô Vĩnh Diện, Thủ Đức', '06:00:00', '22:00:00', 10.8471, 106.7592, 35); SET @r64 = LAST_INSERT_ID();
INSERT INTO restaurants (merchant_id, name, address, open_time, close_time, latitude, longitude, max_pending_orders) VALUES (@m10, 'Pizza Hut - Võ Văn Ngân', 'Võ Văn Ngân, Thủ Đức', '10:00:00', '22:00:00', 10.8485, 106.7561, 30); SET @r65 = LAST_INSERT_ID();


-- =============================================================================
-- 4. TẠO 300 MÓN ĂN MỚI (PHÂN BỔ: 20 QUÁN ĐẦU CÓ 7 MÓN, 20 QUÁN SAU CÓ 8 MÓN)
-- =============================================================================

-- --- NHÓM 20 CỬA HÀNG ĐẦU (R26 ĐẾN R45 - 7 MÓN MỖI QUÁN = 140 MÓN) ---
INSERT INTO foods (restaurant_id, category_id, name, description, price) VALUES
(@r26, @cat10, 'Sủi Cảo Tôm Thịt Hấp', 'Sủi cảo bọc tôm tươi viên lớn hấp mềm', 48000.00),
(@r26, @cat10, 'Sủi Cảo Chiên Giòn', 'Sủi cảo chiên vàng rụm ăn cùng tương xí muội', 50000.00),
(@r26, @cat10, 'Sủi Cảo Thập Cẩm Tô Lớn', 'Sủi cảo nấu cùng bóng cá, mực và gan heo', 65000.00),
(@r26, @cat10, 'Mì Sủi Cảo Hồng Kông', 'Sợi mì trứng tươi kéo tay kèm 3 viên sủi cảo', 55000.00),
(@r26, @cat10, 'Súp Sủi Cảo Rau Cải', 'Súp sủi cảo nước dùng xương hầm thanh ngọt', 48000.00),
(@r26, @cat10, 'Hoành Thánh Chiên Giòn', 'Đĩa 5 chiếc hoành thánh chiên ăn vặt', 30000.00),
(@r26, @cat6, 'Nước Sâm Lạnh Nhà Nấu', 'Nước sâm mát gan ít đường hảo hạng', 12000.00),

(@r27, @cat7, 'Chè Mè Đen Nóng', 'Chè mè đen xay mịn, vị ngọt nhẹ truyền thống Hoa', 25000.00),
(@r27, @cat7, 'Chè Hạnh Nhân Đậu Hũ', 'Đậu hũ hạnh nhân bùi béo, thơm nồng nàn', 28000.00),
(@r27, @cat7, 'Chè Quả Thông Thảo Mộc', 'Hạt thông hầm cùng các loại thảo dược bổ dưỡng', 35000.00),
(@r27, @cat7, 'Sâm Bổ Lượng Thanh Mát', 'Đầy đủ rong biển, nhãn nhục, hạt sen, củ năng', 30000.00),
(@r27, @cat7, 'Quy Linh Cao Sốt Mật Ong', 'Thạch quy linh cao đắng nhẹ quyện mật ong rừng', 25000.00),
(@r27, @cat7, 'Bánh Ngọt Hồng Kông', 'Bánh nướng xốp mềm ăn kèm khi thưởng chè', 20000.00),
(@r27, @cat6, 'Trà Hoa Cúc Kỷ Tử', 'Tách trà hoa cúc nóng thanh lọc cơ thể', 18000.00),

(@r28, @cat1, 'Cơm Gà Hải Nam Luộc', 'Thịt gà luộc da vàng óng đượm sốt tỏi gừng', 55000.00),
(@r28, @cat1, 'Cơm Gà Hấp Muối Đông Nguyên', 'Cơm gà hấp muối da giòn thịt ngọt lịm', 58000.00),
(@r28, @cat1, 'Cơm Phá Lấu Khìa Nước Dừa', 'Tai heo, lòng heo khìa kẹo chuẩn vị', 48000.00),
(@r28, @cat1, 'Canh Xà Lách Xoong Sườn Non', 'Chén canh xà lách xoong nấu sườn ống', 15000.00),
(@r28, @cat1, 'Đùi Gà Luộc Thêm', 'Phần đùi gà góc tư luộc gọi thêm', 35000.00),
(@r28, @cat1, 'Lòng Gà Xào Mướp Hương', 'Lòng mề gà xào mướp thanh đạm cực bắt cơm', 25000.00),
(@r28, @cat6, 'Trà Đá Siêu To Khổng Lồ', 'Ly trà đá đậm vị trà xanh giải khát', 5000.00),

(@r29, @cat3, 'Hủ Tiếu Mì Sườn Sụn', 'Hủ tiếu mì nấu cùng sườn sụn heo ninh nhừ', 55000.00),
(@r29, @cat3, 'Hủ Tiếu Nam Vang Khô Đậm Vị', 'Hủ tiếu khô trộn nước sốt hắc xì dầu đặc chế', 50000.00),
(@r29, @cat3, 'Mì Trứng Xá Xíu Thượng Hạng', 'Mì trứng ăn kèm xá xíu bản lớn mật ong', 48000.00),
(@r29, @cat3, 'Hủ Tiếu Mực Ống Tươi', 'Mực ống giòn sần sật chan nước dùng mực tươi', 55000.00),
(@r29, @cat3, 'Chén Hoành Thánh Súp Thêm', '4 viên hoành thánh thịt bằm nước lèo', 18000.00),
(@r29, @cat3, 'Xí Quách Heo Bản Lớn', 'Cục xí quách nhiều thịt tủy béo ngậy', 25000.00),
(@r29, @cat6, 'Hồng Trà Tắc Đá', 'Hồng trà pha tắc chua ngọt sảng khoái', 15000.00),

(@r30, @cat10, 'Lẩu Đầu Cá Tá Lả (Cỡ Vừa)', 'Lẩu đầu cá hồi nấy ngót cùng thì là, cà chua', 189000.00),
(@r30, @cat10, 'Ruột Cá Hấp Hành Tiêu', 'Phần ruột cá basa làm sạch hấp hành dẻo dai', 50000.00),
(@r30, @cat7, 'Cá Viên Chiên Nước Mắm Khô', 'Cá viên chiên xào sốt mắm tỏi ăn kèm lẩu', 40000.00),
(@r30, @cat3, 'Vắt Mì Trứng Ăn Kèm Lẩu', 'Mì tươi sợi nhỏ nhúng lẩu', 10000.00),
(@r30, @cat10, 'Đĩa Rau Muống Chẻ Tươi', 'Rau muống bào sợi ăn kèm nồi lẩu', 12000.00),
(@r30, @cat10, 'Nước Cốt Lẩu Nấu Ngót Thêm', 'Chén nước lẩu châm thêm khi cạn', 15000.00),
(@r30, @cat6, 'Coca Cola Lon Ưp Lạnh', 'Nước ngọt giải béo khi ăn lẩu cá', 18000.00),

(@r31, @cat2, 'Bánh Bao Xá Xíu Thọ Phát', 'Bánh bao nhân thịt xá xíu đậm đà cỡ lớn', 22000.00),
(@r31, @cat2, 'Bánh Bao 2 Trứng Muối', 'Nhân thịt bằm bọc hai quả trứng muối siêu bùi', 28000.00),
(@r31, @cat2, 'Bánh Bao Chay Sữa Dừa', 'Bánh bao chay hương vani sữa dừa thơm mịn', 12000.00),
(@r31, @cat2, 'Bánh Bao Kim Sa Tan Chảy', 'Gói 2 chiếc bánh bao nhỏ nhân trứng muối chảy', 25000.00),
(@r31, @cat6, 'Sữa Đậu Nành Nguyên Chất', 'Sữa đậu nành nóng/đá nấu thủ công ít đường', 12000.00),
(@r31, @cat6, 'Sữa Bắp Nếp Thơm Ngọt', 'Sữa bắp nếp miền tây sánh mịn béo bùi', 15000.00),
(@r31, @cat10, 'Há Cảo Heo Hấp (Hộp 4 cái)', 'Há cảo nhân thịt heo bằm ăn kèm sốt tương', 24000.00),

(@r32, @cat7, 'Phá Lấu Bò Truyền Thống', 'Chén phá lấu lòng bò nước cốt dừa béo ngậy', 32000.00),
(@r32, @cat7, 'Phá Lấu Mì Gói Trộn', 'Mì gói trụng trộn phá lấu khô độc đáo', 38000.00),
(@r32, @cat2, 'Phá Lấu Ăn Kèm Bánh Mì', 'Combo chén phá lấu lớn kèm ổ bánh mì giòn', 35000.00),
(@r32, @cat7, 'Phá Lấu Xào Me Chua Cay', 'Lòng bò xào sốt me đặc sánh ăn cực cuốn', 40000.00),
(@r32, @cat7, 'Mề Gà Nướng Muối Ớt (2 xiên)', 'Mề gà nướng than hoa giòn rụm cay nồng', 24000.00),
(@r32, @cat7, 'Chân Gà Sả Tắc Ngâm', 'Hộp 4 chân gà rút xương ngâm sả tắc', 35000.00),
(@r32, @cat6, 'Trà Tắc Đá Đường', 'Thức uống bình dân đập tan cơn khát', 12000.00),

(@r33, @cat1, 'Vịt Quay Tiêu Tô Châu (1/4 con)', 'Vịt quay da giòn đẫm sốt tiêu đen Macau', 95000.00),
(@r33, @cat1, 'Heo Quay Bánh Hỏi Mỡ Hành', '100g heo quay giòn bì ăn kèm bánh hỏi rau sống', 60000.00),
(@r33, @cat1, 'Phá Lấu Vịt Chợ Lớn', 'Phá lấu thịt vịt và huyết vịt hầm dẻo', 45000.00),
(@r33, @cat1, 'Đĩa Dưa Leo Đồ Chua Thêm', 'Đồ ăn kèm chống ngấy cho vịt béo', 8000.00),
(@r33, @cat1, 'Nước Sốt Vịt Quay Gọi Thêm', 'Chén nước sốt đặc sánh chấm bánh bao', 5000.00),
(@r33, @cat2, 'Bánh Bao Chiên Ăn Kèm (3 cái)', 'Bánh bao không nhân chiên vàng giòn', 15000.00),
(@r33, @cat6, 'Trà Đá Hoa Lài Lạnh', 'Trà đá thơm hương hoa lài tươi', 5000.00),

(@r34, @cat9, 'Pizza Margherita Truyền Thống', 'Sốt cà chua, phô mai Mozzarella và lá húng tây', 150000.00),
(@r34, @cat9, 'Pizza 4 Loại Phô Mai Trứ Danh', 'Sự kết hợp phô mai cao cấp kèm mật ong rừng', 240000.00),
(@r34, @cat9, 'Pizza Gà Teriyaki Kiểu Nhật', 'Thịt gà sốt teriyaki, rong biển và sốt mayo', 195000.00),
(@r34, @cat9, 'Salad Burrata Trái Cây', 'Phô mai Burrata tươi béo ngậy kèm trái cây nhiệt đới', 135000.00),
(@r34, @cat9, 'Mì Ý Sốt Ghẹ Cà Chua', 'Mì Spaghetti xào thịt ghẹ tươi và sốt kem cà chua', 165000.00),
(@r34, @cat6, 'Tiramisu Vị Truyền Thống', 'Bánh kem cà phê béo ngậy tan chảy đầu lưỡi', 55000.00),
(@r34, @cat6, 'Nước Khoáng San Pellegrino', 'Nước khoáng có ga nhập khẩu từ Ý', 45000.00),

(@r35, @cat4, 'Combo 2 Miếng Gà Giòn Cay', '2 miếng gà rán giòn rụm đậm vị cay tẩm bột', 78000.00),
(@r35, @cat4, 'Burger Gà Phi-lê Phô Mai', 'Burger nhân ức gà giòn phi lê và phô mai cheddar', 52000.00),
(@r35, @cat4, 'Khoai Tây Chiên Cajun Cỡ Lớn', 'Khoai tây cọng tẩm muối vị Cajun xóc đều', 35000.00),
(@r35, @cat4, 'Gà Không Xương 3 Miếng (Tenders)', 'Ức gà phi lê cắt miếng chiên giòn ráo dầu', 45000.00),
(@r35, @cat4, 'Bắp Cải Trộn Coleslaw Nhỏ', 'Bắp cải xắt nhuyễn sốt mayo chua ngọt giải ngấy', 18000.00),
(@r35, @cat6, 'Pepsi Lon Mát Lạnh', 'Nước ngọt có ga ăn kèm gà rán', 18000.00),
(@r35, @cat6, 'Kem Nón Vani Ốc Quế', 'Kem cây vani sữa tươi ngọt lịm', 12000.00),

(@r36, @cat3, 'Mẹt Bún Đậu Đầy Đủ Homemade', 'Đậu hũ chiên, chả cốm, nem chua rán, chân giò luộc', 68000.00),
(@r36, @cat3, 'Chả Cốm Hà Nội Chiên Thêm', 'Khoanh chả cốm dẻo thơm nồng đượm vị cốm', 18000.00),
(@r36, @cat3, 'Nem Chua Rán Giòn Rụm', 'Đĩa 4 cây nem chua tẩm bột xù chiên nóng', 32000.00),
(@r36, @cat3, 'Lòng Dồi Heo Luộc Thập Cẩm', 'Dồi trường, gan, phèo non luộc chấm mắm tôm', 45000.00),
(@r36, @cat3, 'Thịt Chân Giò Luộc Sắp Lát', 'Thịt heo chân giò cuộn chỉ luộc dầy dặn', 35000.00),
(@r36, @cat6, 'Nước Sấu Đá Hà Nội', 'Quả sấu ngâm đường chua ngọt thanh mát', 18000.00),
(@r36, @cat6, 'Nước Mơ Ngâm Đường Phố Cổ', 'Nước mơ chua thanh mặn dịu sảng khoái', 18000.00),

(@r37, @cat1, 'Ribeye Steak Úc 250g', 'Thịt đầu thăn ngoại bò Úc nướng sốt tiêu xanh', 450000.00),
(@r37, @cat1, 'Striploin Steak Mỹ 250g', 'Thịt thăn ngoại bò Mỹ mềm tan nướng sốt nấm', 520000.00),
(@r37, @cat1, 'Khoai Tây Nghiền Sốt Bơ Pháp', 'Khoai tây nghiền mịn màng, béo ngậy mùi bơ', 55000.00),
(@r37, @cat1, 'Nấm Rơm Xào Tỏi Bơ', 'Nấm tươi xào lửa lớn đượm hương tỏi lý sơn', 48000.00),
(@r37, @cat1, 'Salad Sốt Dầu Giấm Balsamic', 'Rau xanh tổng hợp xóc giấm đen đậm vị Ý', 50000.00),
(@r37, @cat6, 'Ly Rượu Vang Đỏ House Wine', 'Rượu vang đỏ cao cấp nhập khẩu nâng tầm món ăn', 120000.00),
(@r37, @cat6, 'Bánh Kem Chocolate Lava', 'Bánh ngọt socola nướng lòng chảy ấm áp', 65000.00),

(@r38, @cat8, 'Combo Sushi Hoàng Gia (12 viên)', 'Đầy đủ sushi tôm, cá hồi, lươn nhật, trứng cá', 240000.00),
(@r38, @cat8, 'Sashimi Cá Hồi Phi Lê Na-uy', '5 lát cá hồi tươi sống cắt dầy kèm mù tạt', 155000.00),
(@r38, @cat8, 'Cali Roll Cuộn Trứng Cá Chuồn', 'Cơm cuộn thanh cua, dưa leo phủ trứng cá ruốc', 85000.00),
(@r38, @cat8, 'Mì Udon Thịt Bò Mỹ Cốt Súp', 'Mì sợi dày Nhật Bản nấu cùng thịt bò lát', 120000.00),
(@r38, @cat8, 'Súp Miso Rong Biển Đậu Hũ', 'Súp đậu tương thanh nhẹ ăn ấm bụng', 25000.00),
(@r38, @cat8, 'Đậu Nành Nhật Edamame Luộc', 'Đậu nành luộc rắc muối biển ăn chơi', 30000.00),
(@r38, @cat6, 'Trà Xanh Nhật Bản Đá Không Đường', 'Cốt trà xanh Matcha thanh lọc cơ thể', 15000.00),

(@r39, @cat5, 'Trà Sữa Trân Châu Đen (Size M)', 'Trà sữa truyền thống đậm vị trà cùng trân châu đen', 55000.00),
(@r39, @cat5, 'Trà Xanh Đào Alisan Mát Lạnh', 'Trà Alisan hương đào tươi thơm mát mùa hè', 52000.00),
(@r39, @cat5, 'Trà Alisan Kem Sữa Milkfoam', 'Lớp kem sữa béo mặn phủ trên cốt trà Alisan', 58000.00),
(@r39, @cat5, 'Trà Đen Sương Sáo Cắt Nhỏ', 'Trà đen thanh ngọt hòa quyện sương sáo mềm', 50000.00),
(@r39, @cat5, 'Thêm Thạch Trân Châu Trắng Giòn', 'Topping trân châu trắng sần sật dai dai', 10000.00),
(@r39, @cat5, 'Thêm Thạch Ai Yu Thanh Mát', 'Thạch chanh đài loan mềm mịn trơn tuột', 10000.00),
(@r39, @cat5, 'Sữa Tươi Trân Châu Đường Đen', 'Sữa tươi Meiji nguyên chất kết hợp đường mật', 62000.00),

( @r40, @cat3, 'Phở Bò Tái Nạm Bản Lớn', 'Bánh phở mềm, thịt bò tái hoa kèm nạm bò chín', 60000.00),
( @r40, @cat3, 'Phở Bò Sốt Vang Kiểu Bắc', 'Thịt nạm bò hầm gấc và rượu vang thơm nức', 65000.00),
( @r40, @cat3, 'Phở Gà Xe Hành Lá Ngập Tràn', 'Thịt gà ta xé phay da giòn nức tiếng', 55000.00),
( @r40, @cat3, 'Quẩy Chiên Nóng Giòn Rụm', 'Đĩa 3 chiếc quẩy đùi gà giòn ăn kèm phở', 10000.00),
( @r40, @cat3, 'Trứng Chần Sốt Nước Tiết Bò', 'Chén trứng lòng đào chần súp bổ dưỡng', 12000.00),
( @r40, @cat3, 'Tiết Bò Luộc Chén Nhỏ', 'Chén tiết heo/bò mềm như thạch gọi thêm', 8000.00),
( @r40, @cat6, 'Trà Đá Hoa Lài Thanh Mát', 'Ly trà đá mát lạnh hương lài thơm ngát', 5000.00),

(@r41, @cat2, 'Bánh Mì Thịt Nguội Truyền Thống', 'Ổ bánh mì nhân chả lụa, dăm bông, bơ, pate dầy', 35000.00),
(@r41, @cat2, 'Bánh Mì Gà Xé Xá Xíu Mật Ong', 'Nhân thịt gà xé tơi đượm nước sốt mật ong kẹo', 32000.00),
(@r41, @cat2, 'Bánh Mì 2 Trứng Ốp La Giòn Rìa', '2 trứng chiên lòng đào xịt nước tương hành ngò', 25000.00),
(@r41, @cat2, 'Hộp Pate Bơ Nhỏ Nhập Khẩu', 'Pate gan heo mịn màng béo ngậy ăn thêm', 15000.00),
(@r41, @cat2, 'Chà Bông Heo Nhà Làm (Túi 50g)', 'Ruốc chà bông heo dai dai ngọt thịt tự nhiên', 20000.00),
(@r41, @cat6, 'Nước Suối Tinh Khiết Aquafina', 'Chai nước suối 500ml ướp đá sẵn', 10000.00),
(@r41, @cat6, 'Sữa Đậu Nành Fami Hộp Giấy', 'Sữa đậu nành đóng hộp thơm ngon tiện lợi', 12000.00),

(@r42, @cat1, 'Cơm Niêu Cá Kho Tộ Đậm Vị', 'Cơm cháy niêu đất ăn kèm cá lóc kho tộ kẹo', 75000.00),
(@r42, @cat1, 'Cơm Niêu Sườn Non Rim Tiêu', 'Sườn non heo rim mặn ngọt keo chuẩn vị cơm mẹ nấu', 70000.00),
(@r42, @cat1, 'Canh Cua Đồng Rau Đay Mồng Tơi', 'Bát canh cua đồng nhiều gạch béo mát ngày hè', 35000.00),
(@r42, @cat1, 'Chén Cà Pháo Chấm Mắm Tôm', 'Cà pháo muối giòn rụm đượm mắm tôm chanh ớt', 15000.00),
(@r42, @cat1, 'Thịt Ba Chỉ Luộc Sắp Đĩa', 'Thịt heo ba chỉ luộc vừa chín tới dầy mỡ mỏng da', 45000.00),
(@r42, @cat1, 'Mẹt Rau Luộc Chấm Kho Quẹt', 'Bầu, đậu bắp, rau cải luộc chấm kho quẹt tóp mỡ', 40000.00),
(@r42, @cat6, 'Trà Đá Mát Lạnh Sảng Khoái', 'Ly trà đá pha đậm vị trà xanh búp lộc', 5000.00),

(@r43, @cat3, 'Suất Bún Chả Hà Nội Truyền Thống', 'Đầy đủ chả băm, chả miếng nướng than kèm đu đủ xanh', 55000.00),
(@r43, @cat3, 'Nem Hải Sản Giòn Rụm (2 chiếc)', 'Nem nhân tôm mực phô mai tẩm bột chiên xù', 30000.00),
(@r43, @cat3, 'Suất Chả Băm Nướng Gọi Thêm', '3 viên chả băm nướng cháy cạnh thơm lừng', 25000.00),
(@r43, @cat3, 'Suất Chả Miếng Nướng Thêm', 'Đĩa thịt ba chỉ nướng than hoa đượm mật', 25000.00),
(@r43, @cat3, 'Đĩa Bún Tươi Lá Gọi Thêm', 'Bún sợi nhỏ ép lá sạch sẽ ăn kèm', 8000.00),
(@r43, @cat6, 'Nước Sấu Đá Chuẩn Vị Bắc', 'Quả sấu ngâm giòn ngọt pha đá mát lạnh', 18000.00),
(@r43, @cat6, 'Nước Quất Mật Ong Ấm Áp', 'Nước tắc mật ong rừng thanh giọng ấm bụng', 18000.00),

(@r44, @cat3, 'Bún Nước Tôm Bò Đặc Biệt Bình Định', 'Tôm quết tươi và thịt bò bắp tái hoa chan súp', 55000.00),
(@r44, @cat3, 'Mì Trộn Muối Ớt Trứng Lòng Đào', 'Mì gói trộn sốt muối ớt siêu cay kèm chén súp', 50000.00),
(@r44, @cat3, 'Chén Trứng Chần Thêm Siêu Bổ', 'Quả trứng gà ta lòng đào chần nước súp hành', 10000.00),
(@r44, @cat3, 'Thịt Bò Tái Nhúng Súp Thêm', '50g thịt bò bắp tái mềm tan gọi thêm', 25000.00),
(@r44, @cat3, 'Tôm Tươi Lột Vỏ Trụng Chén', '3 con tôm đất ngọt thịt trụng vừa chín tới', 20000.00),
(@r44, @cat3, 'Cây Chả Lụa Nha Trang Gói Lá', 'Chả cá/chả lụa nha trang dai giòn ăn thêm', 10000.00),
(@r44, @cat6, 'Nước Sâm Dứa Đá Sài Gòn', 'Thức uống hương sâm dứa sữa ngọt dịu thanh mát', 12000.00),

(@r45, @cat5, 'Trà Sữa Truyền Thống Phúc Long (Size M)', 'Đậm vị trà đen huyền thoại hòa cốt sữa đặc béo', 55000.00),
(@r45, @cat5, 'Trà Lài Đác Thơm Signature', 'Cốt trà lài thanh khiết cùng hạt đác rim thơm ngọt', 60000.00),
(@r45, @cat5, 'Trà Đào Phúc Long Đậm Vị', 'Cốt trà hồng trà hảo hạng kèm 3 miếng đào dầy', 55000.00),
(@r45, @cat6, 'Cà Phê Sữa Đá Phin Đậm Đặc', 'Robusta rang đậm nhỏ giọt nguyên chất chuẩn gu Việt', 45000.00),
(@r45, @cat6, 'Bánh Mousse Trà Xanh Mịn Màng', 'Bánh ngọt cốt kem matcha nhật mềm xốp lịm', 35000.00),
(@r45, @cat6, 'Bánh Croissant Bơ Tỏi Nướng', 'Bánh sừng bò ngàn lớp thơm phức bơ tỏi', 28000.00),
(@r45, @cat5, 'Hồng Trà Sữa Kem Béo Ngậy', 'Trà đen Ceylon pha sữa tươi phủ milkfoam dầy', 58000.00);

-- --- NHÓM 20 CỬA HÀNG SAU (R46 ĐẾN R65 - 8 MÓN MỖI QUÁN = 160 MÓN) ---
INSERT INTO foods (restaurant_id, category_id, name, description, price) VALUES
(@r46, @cat4, 'Gà Nướng Sốt Tiêu Xanh Tây Nguyên', 'Nguyên con gà nướng đất sét đẫm hạt tiêu xanh tươi', 189000.00),
(@r46, @cat4, 'Gà Nướng Mật Ong Rừng Giòn Da', 'Gà quay lu quét mật ong rừng vàng ruộm ngọt thịt', 195000.00),
(@r46, @cat1, 'Đĩa Xôi Mỡ Hành Ăn Kèm Gà', 'Xôi nếp nương dẻo quánh rưới mỡ hành tóp mỡ', 20000.00),
(@r46, @cat10, 'Súp Gà Nấm Đông Cô Ấm Áp', 'Súp thịt gà xé phay nấu nấm hương bách thảo', 30000.00),
(@r46, @cat7, 'Chân Gà Quái Thú Chiên Mắm', 'Hộp 4 chân gà chiên mắm sốt tỏi ớt siêu cay', 45000.00),
(@r46, @cat1, 'Lòng Mề Gà Xào Mướp Hương Đĩa', 'Lòng mề trút xào lửa lớn giữ độ giòn sần sật', 35000.00),
(@r46, @cat1, 'Đĩa Cà Chua Dưa Leo Chấm Muối', 'Đồ rau xanh tươi mát ăn kèm đỡ ngán gà', 12000.00),
(@r46, @cat6, 'Trà Chanh Dây Hạt Chia Ly Lớn', 'Nước cốt chanh dây chua ngọt xóc hạt chia bổ dưỡng', 18000.00),

(@r47, @cat3, 'Bánh Canh Cua Đặc Biệt Út Lệ', 'Sợi bánh canh bột lọc, thịt cua tuyết dầy dặn, huyết, tôm', 55000.00),
(@r47, @cat3, 'Bánh Canh Tôm Giò Heo Khoanh', 'Giò khoanh heo nạc ninh nhừ cùng tôm đất ngọt', 50000.00),
(@r47, @cat3, 'Quẩy Chiên Giòn Bản Lớn', 'Cặp 2 chiếc quẩy đùi gà siêu giòn ăn kèm nước súp cua', 10000.00),
(@r47, @cat3, 'Thịt Càng Cua Bóc Vỏ Thêm', 'Phần thịt càng cua tươi bóc sẵn gọi thêm vào tô', 30000.00),
(@r47, @cat3, 'Giò Gân Heo Ninh Nhừ Thêm', 'Một cục giò gân heo dai sần sật gọi ăn thêm', 20000.00),
(@r47, @cat3, 'Chả Cá Vũng Tàu Hấp Đĩa', 'Chả cá thác lác quét dai hấp chín xắt miếng', 15000.00),
(@r47, @cat6, 'Nước Sâm Mía Lau Nhà Nấu', 'Nước mía lau hầm rễ tranh mã đề giải nhiệt tốt', 12000.00),
(@r47, @cat6, 'Trà Đá Hoa Lài Đá Lạnh', 'Ly trà đá giải khát hương hoa nhài thanh khiết', 5000.00),

(@r48, @cat7, 'Mẹt Nem Nướng Đà Lạt Đầy Đủ', 'Nem nướng phết mật ong, ram giòn, bánh tráng, rau sống', 65000.00),
(@r48, @cat7, 'Bánh Ram Dẻo Chiên Giòn Thêm', 'Xấp 5 thanh bánh đa ram chiên cuốn giòn rụm', 15000.00),
(@r48, @cat7, 'Nem Nướng Cây Gọi Thêm', '2 cây nem nướng heo bằm bản lớn nướng than', 24000.00),
(@r48, @cat2, 'Bánh Tráng Cuốn Dẻo Thêm', 'Xấp bánh tráng phơi sương trảng bàng dẻo dai thêm', 10000.00),
(@r48, @cat7, 'Mẹt Rau Sống Đủ Loại Thêm', 'Rau dưa hẹ tươi sạch gọi thêm thoải mái', 12000.00),
(@r48, @cat7, 'Chai Nước Chấm Tương Đậu Độc Quyền', 'Nước sốt chấm tương đậu phộng xay nhuyễn ấm nóng', 10000.00),
(@r48, @cat6, 'Sữa Đậu Nành Nóng Đà Lạt', 'Ly sữa đậu nành nóng hổi thơm mùi lá dứa', 15000.00),
(@r48, @cat6, 'Sữa Bắp Nếp Luộc Thơm Lừng', 'Sữa bắp nếp đóng chai nguyên chất bổ dưỡng', 15000.00),

(@r49, @cat6, 'Cà Phê Năng Lượng Sữa Đá', 'Cà phê Robusta phối Arabica pha phin sữa đặc sánh', 39000.00),
(@r49, @cat6, 'Cà Phê Sâm Nhung Bổ Dưỡng', 'Sự kết hợp phá cách cà phê đen cùng tinh chất sâm quý', 55000.00),
(@r49, @cat6, 'Espresso Đậm Vị Ý Máy Pha', 'Hạt Arabica chiết suất áp suất cao thơm ngút ngàn', 45000.00),
(@r49, @cat2, 'Bánh Mì Nướng Bơ Tỏi Giòn', '3 lát bánh mì baguette nướng bơ tỏi lá thơm', 25000.00),
(@r49, @cat2, 'Croissant Phô Mai Tan Chảy', 'Bánh sừng bò ngàn lớp nhân phô mai chảy nóng hổi', 35000.00),
(@r49, @cat6, 'Trà Thảo Mộc Cung Đình Huế', 'Tách trà ấm tổng hợp kỷ tử, táo đỏ, hoa cúc', 40000.00),
(@r49, @cat6, 'Trà Vải Hạt Chia Sảng Khoái', 'Cốt trà lài tươi kèm 3 quả vải ngâm giòn dầy', 45000.00),
(@r49, @cat6, 'Hộp Cà Phê Hòa Tan G7 (20 gói)', 'Cà phê hòa tan thuận tiện mang đi xa mua thêm', 60000.00),

(@r50, @cat10, 'Lẩu Gà Lá É Phú Yên (Cỡ Vừa)', 'Thịt gà ta nửa con, nước lẩu măng chua, dĩa lá é tươi', 199000.00),
(@r50, @cat10, 'Thịt Gà Ta Thả Lẩu Gọi Thêm', 'Phần nửa con gà ta chặt khúc nhúng thêm', 95000.00),
(@r50, @cat10, 'Đĩa Lá É Tươi Đặc Sản', 'Lá é trắng thơm nồng giã nhuyễn nhúng thêm', 15000.00),
(@r50, @cat3, 'Đĩa Bún Tươi Ăn Kèm Lẩu', 'Bún tươi sợi nhỏ ăn cùng nước súp cay ấm', 10000.00),
(@r50, @cat10, 'Nấm Bào Ngư Xám Nhúng Lẩu', 'Nấm tươi ngọt lịm hút nước súp gà siêu ngon', 20000.00),
(@r50, @cat7, 'Lòng Gà Trứng Non Xào Sả Ớt', 'Đĩa lòng mề gà trứng non vàng ươm ăn cuốn bún', 65000.00),
(@r50, @cat6, 'Trà Tắc Xô Khổng Lồ 1 Lít', 'Xô trà tắc mát lạnh giải nhiệt cấp tốc', 20000.00),
(@r50, @cat6, 'Sữa Đậu Nành Đá Đường Lá Dứa', 'Ly sữa đậu nành mát lạnh ngọt thanh', 15000.00),

(@r51, @cat3, 'Mẹt Bún Đậu Đặc Biệt Mạc Văn Khoa', 'Đầy đủ đậu rán giòn, chả cốm, dồi sụn, heo luộc', 69000.00),
(@r51, @cat3, 'Đậu Hũ Chiên Lướt Ván Thêm', 'Khay đậu hũ cắt khối chiên ngoài giòn trong mềm mịn', 15000.00),
(@r51, @cat3, 'Chả Cốm Hà Nội Chiên Vàng', 'Khoanh chả cốm đặc sản chuẩn vị thủ đô gọi thêm', 18000.00),
(@r51, @cat3, 'Dồi Sụn Nướng Than Hoa Thơm', 'Đĩa dồi sụn heo giòn sần sật nướng cháy cạnh', 35000.00),
(@r51, @cat3, 'Thịt Bắp Giò Heo Luộc Sắp Lát', 'Thịt chân giò bó tròn luộc vừa chín tới dầy miếng', 35000.00),
(@r51, @cat3, 'Đĩa Bún Lá Sạch Gọi Thêm', 'Suất bún tươi ép bánh gọi thêm ăn no bụng', 8000.00),
(@r51, @cat3, 'Chén Mắm Tôm Thanh Hóa Pha Sẵn', 'Mắm tôm đặc sản rưới dầu nóng tỏi ớt đường tắc', 5000.00),
(@r51, @cat6, 'Nước Chanh Sả Tắc Đá Thần Thánh', 'Sự hòa quyện chanh tươi cùng cốt sả ấm nồng đá lạnh', 18000.00),

(@r52, @cat1, 'Bò Né Đặc Biệt Chảo Gang Nóng', 'Thịt bò Mỹ phi-lê thái lát, trứng ốp la, paté, xúc xích', 45000.00),
(@r52, @cat1, 'Bò Né Trứng Xúc Xích Phổ Thông', 'Phần bò né cơ bản cho bữa sáng nhanh gọn', 38000.00),
(@r52, @cat2, 'Bánh Mì Đặc Ruột Ăn Kèm Giòn', 'Ổ bánh mì đặc ruột nướng nóng giòn chấm sốt bơ', 5000.00),
(@r52, @cat4, 'Khoai Tây Chiên Cọng Giòn Rụm', 'Khoai tây cọng chiên ráo dầu lắc muối nhẹ', 25000.00),
(@r52, @cat1, 'Sốt Bơ Paté Gan Heo Gọi Thêm', 'Chén bơ sữa kết hợp paté béo ngậy quẹt bánh mì', 12000.00),
(@r52, @cat1, 'Xíu Mại Sốt Cà Chua Chén Nhỏ', 'Viên xíu mại thịt heo bằm đẫm sốt cà chua ngọt', 15000.00),
(@r52, @cat1, 'Đĩa Salad Dầu Giấm Thanh Chua', 'Rau xà lách, cà chua, củ hành tây xóc dầu giấm', 15000.00),
(@r52, @cat6, 'Pepsi Tươi Máy Pha Mát Lạnh', 'Ly nước ngọt có ga đập tan cơn khát', 12000.00),

(@r53, @cat3, 'Mì Quảng Gà Ta Trứng Lòng Đào', 'Thịt gà ta kho nghệ đậm đà, trứng lòng đào dẻo bùi', 55000.00),
(@r53, @cat3, 'Mì Quảng Tôm Thịt Trứng Cút', 'Combo truyền thống nhân tôm đất, thịt ba chỉ, trứng cút', 50000.00),
(@r53, @cat3, 'Mì Quảng Ếch Đồng Sốt Sả Nghệ', 'Thịt ếch đồng săn chắc um nghệ vàng thơm nức mũi', 60000.00),
(@r53, @cat7, 'Bánh Tráng Nướng Quảng Nam (1 cái)', 'Bánh đa mè đen nướng giòn rụm bẻ nhỏ ăn kèm mì', 10000.00),
(@r53, @cat3, 'Chén Nước Súp Mì Quảng Gọi Thêm', 'Nước súp hầm xương ống cô đặc đậm đà chan thêm', 10000.00),
(@r53, @cat3, 'Đĩa Rau Sống Bắp Chuối Sợi Thêm', 'Rau sống bắp chuối bào, cải mầm tươi ngon sạch sẽ', 12000.00),
(@r53, @cat7, 'Trái Ớt Xanh Miền Trung Cắn Kèm', 'Ớt xiêm xanh cay nồng thơm đặc trưng miền Trung', 3000.00),
(@r53, @cat6, 'Nước Chè Xanh Ấm Bụng', 'Ly trà xanh om lá tươi nóng hổi chát nhẹ thanh mát', 5000.00),

(@r54, @cat7, 'Bánh Tráng Nướng Thập Cẩm Phô Mai', 'Nhân bò khô, xúc xích, trứng cút, hành lá, phô mai kéo sợi', 30000.00),
(@r54, @cat7, 'Bánh Tráng Nướng Bò Khô Trứng Cút', 'Hương vị truyền thống đường phố Đà Lạt giòn tan', 25000.00),
(@r54, @cat7, 'Bánh Tráng Nướng Gà Xé Chà Bông', 'Gà xé cay xé sợi cùng chà bông heo sốt bơ béo', 27000.00),
(@r54, @cat7, 'Bánh Tráng Cuộn Sốt Bơ Me', 'Bánh tráng dẻo cuộn hành phi, tép khô rưới sốt bơ me', 25000.00),
(@r54, @cat7, 'Cá Viên Chiên Xiên Que Thập Cẩm', 'Mẹt cá viên, tôm viên, bò viên chiên ăn vặt', 35000.00),
(@r54, @cat7, 'Bắp Xào Mỡ Hành Tép Khô Ngọt', 'Bắp mỹ tách hạt xào bơ mỡ hành thơm nức', 25000.00),
(@r54, @cat6, 'Trà Đào Đá Miếng Ngọt Lịm', 'Ly trà đào mát lạnh kèm 2 lát đào ngâm giòn', 18000.00),
(@r54, @cat6, 'Sữa Tươi Trân Châu Đường Đen Nhỏ', 'Sữa tươi lắc đường đen mật ngọt lịm béo', 25000.00),

(@r55, @cat7, 'Ốc Hương Xào Trứng Muối Hoàng Kim', 'Ốc hương tươi sống xào sốt trứng muối đặc sánh béo ngậy', 85000.00),
(@r55, @cat7, 'Ốc Móng Tay Xào Rau Muống Tỏi', 'Ốc móng tay dai giòn xào rau muống lửa lớn xanh mướt', 65000.00),
(@r55, @cat7, 'Nghêu Hấp Sả Ớt Cay Nồng Gặp', 'Tô nghêu hấp sả nghi ngút khói nước dùng ngọt lịm', 55000.00),
(@r55, @cat7, 'Hàu Nướng Mỡ Hành Tóp Mỡ (3 con)', 'Hàu sữa tươi rói nướng mỡ hành thơm béo bùi', 45000.00),
(@r55, @cat7, 'Sò Lông Nướng Tiêu Xanh Phú Quốc', 'Sò lông bản lớn nướng nước sốt tiêu xanh cay nồng', 60000.00),
(@r55, @cat7, 'Càng Ghẹ Rang Muối Ớt Tây Ninh', 'Càng ghẹ chắc thịt rang khô muối ớt mặn cay cực dính', 80000.00),
(@r55, @cat2, 'Bánh Mì Chấm Sốt Trứng Muối Giòn', 'Ổ bánh mì nướng nóng để quẹt sốt ốc siêu ngon', 5000.00),
(@r55, @cat6, 'Nước Quất Đường Đá Giải Khát', 'Ly trà tắc chua ngọt trung hòa vị ốc béo', 12000.00),

(@r56, @cat7, 'Ốc Mỡ Xào Sốt Me Cốt Kẹo', 'Ốc mỡ xào me chua ngọt đậm đà chấm bánh mì đỉnh cao', 65000.00),
(@r56, @cat7, 'Ốc Len Xào Nước Cốt Dừa Béo', 'Ốc len tươi hút rột rột hòa quyện nước cốt dừa sánh', 70000.00),
(@r56, @cat7, 'Sò Huyết Cháy Tỏi Lý Sơn Thơm', 'Sò huyết bổ dưỡng xào tỏi phi vàng rụm giòn thơm', 75000.00),
(@r56, @cat7, 'Càng Cúm Núm Rang Muối Hồng', 'Càng cúm núm giòn rụm đập dập thấm muối ớt cay', 60000.00),
(@r56, @cat7, 'Ốc Tỏi Nướng Muối Ớt Siêu Cay', 'Ốc tỏi size lớn thịt dai sần sật nướng muối ớt nồng', 55000.00),
(@r56, @cat3, 'Mì Xào Ốc Móng Tay Rau Cải', 'Mì gói xào ốc móng tay và cải ngọt ăn no bụng', 55000.00),
(@r56, @cat7, 'Hàu Nướng Phô Mai Pháp (3 con)', 'Hàu nướng phủ phô mai Mozzarella đút lò kéo sợi béo', 50000.00),
(@r56, @cat6, 'Coca Cola Lon 320ml Ướp Đá', 'Giải khát sảng khoái sau bữa tiệc hải sản ngon', 18000.00),

(@r57, @cat3, 'Bún Mắm Heo Quay Tôm Mực Cần Thơ', 'Nước cốt mắm linh mắm sặc đậm đà, full hải sản heo quay', 65000.00),
(@r57, @cat3, 'Bún Mắm Đặc Biệt Giò Heo Khoanh', 'Sự phá cách độc đáo kết hợp giò heo nạc vào bún mắm', 60000.00),
(@r57, @cat10, 'Lẩu Mắm Miền Tây Cốt Đặc Sản (Nhỏ)', 'Nồi lẩu mắm ăn kèm mẹt rau đồng dồi dào phong phú', 179000.00),
(@r57, @cat10, 'Mẹt Rau Đắng Bông Súng Thêm', 'Rau đắng, bông súng, kèo nèo, bắp chuối nhúng lẩu mắm', 20000.00),
(@r57, @cat3, 'Khía Thịt Heo Quay Giòn Da Thêm', '50g thịt heo quay xắt miếng gọi thêm vào tô bún', 25000.00),
(@r57, @cat3, 'Tôm Mực Tươi Trụng Gọi Thêm', 'Phần hải sản tươi nhúng thêm cho nồi lẩu phong phú', 35000.00),
(@r57, @cat3, 'Đĩa Bún Tươi Sợi Lớn Gọi Thêm', 'Bún tươi sợi lớn chuẩn vị bún mắm miền Tây thêm', 8000.00),
(@r57, @cat6, 'Nước Mía Cốt Tắc Ép Tươi', 'Ly nước mía ép máy mát lạnh ngọt lịm hương tắc', 10000.00),

(@r58, @cat1, 'Cơm Tấm Sườn Nướng Ngô Quyền', 'Sườn cốt lết heo bản lớn nướng mật ong thơm phức hương vị truyền thống', 45000.00),
(@r58, @cat1, 'Cơm Tấm Ba Rọi Nướng Giòn Bì', 'Thịt ba chỉ nướng cháy cạnh thơm mỡ ngập hành', 45000.00),
(@r58, @cat1, 'Cơm Tấm Bì Chả Trứng Ốp La', 'Combo đầy đủ dinh dưỡng cho ngày mới năng động', 40000.00),
(@r58, @cat1, 'Cơm Tấm Lạp Xưởng Trứng Kho Rục', 'Lạp xưởng mai quế lộ kèm quả trứng vịt kho thấm vị', 38000.00),
(@r58, @cat1, 'Canh Xà Lách Xoong Thịt Bằm Chén', 'Canh giải nhiệt nấu cùng thịt heo bằm ngọt nước', 12000.00),
(@r58, @cat1, 'Chén Mỡ Hành Tóp Mỡ Thêm Giòn', 'Tóp mỡ heo chiên giòn rụm rưới mỡ hành thơm phức', 5000.00),
(@r58, @cat1, 'Sườn Miếng Nướng Mật Ong Thêm', 'Một miếng sườn nướng thêm bản lớn cho tín đồ mê thịt', 30000.00),
(@r58, @cat6, 'Nước Sâm Dứa Hạt Chia Mát Lạnh', 'Sâm dứa sữa thơm ngậy kết hợp hạt chia bổ dưỡng đá lạnh', 15000.00),

(@r59, @cat3, 'Bún Mọc Giò Sống Nấm Hương Chuẩn', 'Mọc tươi dai giòn trộn nấm hương mộc nhĩ thơm phức', 50000.00),
(@r59, @cat3, 'Bún Mọc Thập Cẩm Giò Heo Khoanh', 'Tô lớn đầy đủ mọc hấp, mọc chiên và khoanh giò nạc', 60000.00),
(@r59, @cat3, 'Bún Mọc Chả Lụa Lòng Heo Luộc', 'Sự kết hợp chả lụa dầy miếng kèm lòng heo non làm sạch', 55000.00),
(@r59, @cat3, 'Chén Mọc Chiên Thơm Béo Thêm', '3 viên mọc chiên vàng óng đậm đà gia vị tiêu bắc', 15000.00),
(@r59, @cat3, 'Chén Mọc Hấp Tiêu Bắc Thơm Thêm', '3 viên mọc hấp trắng hồng giữ trọn vị ngọt thịt xương', 15000.00),
(@r59, @cat3, 'Đĩa Rau Sống Dọc Mùng Bào Sợi', 'Dọc mùng tước vỏ bào sợi tươi sạch ăn kèm bún nước', 10000.00),
(@r59, @cat3, 'Quẩy Nóng Giòn Rụm Chén Nhỏ', '3 chiếc quẩy đùi gà nhỏ nướng giòn nhúng nước súp mọc', 10000.00),
(@r59, @cat6, 'Trà Đá Hoa Lài Thanh Mát Ly', 'Trà đá pha sẵn giải khát nhanh chóng mát mẻ', 5000.00),

(@r60, @cat1, 'Cơm Tấm Nhuyễn Sườn Cọng Long Xuyên', 'Cơm tấm hạt nhuyễn nhỏ, sườn cọng chặt hạt lựu kho kẹo dẻo', 42000.00),
(@r60, @cat1, 'Cơm Tấm Thịt Kho Rục Nước Dừa', 'Thịt ba chỉ kho rục cắt nhỏ thấm đẫm nước cốt dừa xiêm', 40000.00),
(@r60, @cat1, 'Cơm Tấm Trứng Kho Lòng Đào Miền Tây', 'Quả trứng vịt kho lòng đào dẻo quánh béo ngậy môi', 35000.00),
(@r60, @cat1, 'Đĩa Dưa Đu Đủ Đậm Vị Ngâm Chua', 'Đu đủ xanh bào sợi ngâm chua ngọt giòn rụm chuẩn miền tây', 5000.00),
(@r60, @cat1, 'Chén Mỡ Hành Nước Sốt Kẹo Ngọt', 'Nước sốt thịt kho keo rưới cơm tấm ăn cực đưa miệng', 5000.00),
(@r60, @cat1, 'Sườn Cọng Nướng Mật Ong Gọi Thêm', 'Phần sườn cọng chặt khúc nướng thêm đậm vị ngọt mật', 25000.00),
(@r60, @cat1, 'Canh Chua Bông Điên Điển Tôm Đất', 'Canh chua bông điên điển nấu tôm đồng thanh mát đúng gu', 18000.00),
(@r60, @cat6, 'Trà Đá Lạnh Buốt Giải Khát', 'Ly trà đá đậm hương vị trà sâm dứa thanh khiết', 5000.00),

(@r61, @cat4, '2 Miếng Gà Rán Giòn Vui Vẻ Jollibee', 'Thương hiệu gà rán giòn rụm mọng nước đặc trưng từ Philippines', 76000.00),
(@r61, @cat4, 'Mì Ý Sốt Bò Bằm Jollibee Cỡ Lớn', 'Mì Spaghetti đẫm sốt cà chua thịt bò bằm ngọt dịu trẻ mê', 45000.00),
(@r61, @cat4, 'Burger Gà Giòn Sốt Mayo Béo', 'Burger nhân phi-lê gà chiên xù xà lách tươi béo ngậy', 35000.00),
(@r61, @cat4, 'Khoai Tây Chiên Cỡ Lớn Vàng Óng', 'Khoai tây cọng lớn chiên giòn ráo dầu chấm tương cà', 32000.00),
(@r61, @cat4, 'Gà Sốt Cay Sài Gòn Đậm Vị', '1 miếng gà rán phủ ngập nước sốt tương ớt chua ngọt cay', 42000.00),
(@r61, @cat6, 'Kem Hình Nón Socola Ốc Quế', 'Kem cây vị socola mát lạnh ngọt ngào dẻo mịn', 12000.00),
(@r61, @cat6, 'Nước Ngọt Sprite Lon Lạnh Buốt', 'Nước giải khát có ga hương chanh sảng khoái tăng vị', 18000.00),
(@r61, @cat4, 'Combo Gà Rán Vui Vẻ Kèm Mì Ý', 'Mẹt tổng hợp gồm 1 miếng gà rán và 1 dĩa mì ý nhỏ no nê', 85000.00),

(@r62, @cat6, 'Kem Cây Vani Siêu Dài Mixue', 'Ốc quế kem vani sữa tươi khổng lồ signature siêu hot', 10000.00),
(@r62, @cat6, 'Trà Chanh Tươi Lạnh Khổng Lồ 1 Lít', 'Ly nước cốt chanh tươi lát dầy giải nhiệt mùa hè cực đã', 15000.00),
(@r62, @cat5, 'Trà Sữa Ba Anh Em Thập Cẩm', 'Trà sữa Đài Loan đầy đủ trân châu đen, thạch dừa, pudding', 30000.00),
(@r62, @cat6, 'Dương Chi Cam Lộ Xoài Bưởi Ngon', 'Thức uống xoài xay cốt dừa thạch lựu bưởi hồng dầy vị', 35000.00),
(@r62, @cat6, 'Trà Đào Bốn Mùa Thạch Dừa Giòn', 'Cốt trà bốn mùa thanh nhẹ kèm thạch dừa dai ngon', 25000.00),
(@r62, @cat6, 'Kem Cốc Trân Châu Đường Đen Béo', 'Kem vani cốc phủ ngập trân châu đường đen mật kẹo ấm', 25000.00),
(@r62, @cat6, 'Trà Xanh Chanh Leo Hạt Chia', 'Sự kết hợp chanh dây tươi mát cùng cốt trà xanh lài', 22000.00),
(@r62, @cat6, 'Kem Cốc Vị Dâu Tây Sữa Chua', 'Kem sữa vị dâu tây chua chua ngọt ngọt cuốn hút', 15000.00),

(@r63, @cat5, 'Trà Sữa Trân Châu Hoàng Gia Tocotoco', 'Trà sữa trân châu đen truyền thống thơm đậm vị sữa', 45000.00),
(@r63, @cat5, 'Trà Xoài Kem Cheese Béo Ngậy', 'Xoài tươi xay nhuyễn phủ lớp kem phô mai Macchiato dầy', 52000.00),
(@r63, @cat5, 'Ô Long Kem Sữa Đặc Biệt Ly', 'Cốt trà ô long mộc châu thanh khiết cùng milkfoam mặn', 48000.00),
(@r63, @cat5, 'Trà Sữa Ba Anh Em Toco Cao Cấp', 'Full topping thạch pudding, trân châu và thạch cỏ ngọt', 50000.00),
(@r63, @cat5, 'Thêm Thạch Pudding Trứng Mịn Màng', 'Topping pudding trứng vàng ươm mềm tan như caramel', 8000.00),
(@r63, @cat5, 'Thêm Thạch Trân Châu Sợi Dai', 'Topping trân châu tạo hình sợi dài dẻo dai độc đáo', 8000.00),
(@r63, @cat6, 'Trà Bưởi Mật Ong Thanh Mát Gan', 'Cốt trà đen kết hợp tép bưởi hồng khìa mật ong rừng', 38000.00),
(@r63, @cat5, 'Sữa Tươi Matcha Đậu Đỏ Nhật Bản', 'Trà xanh matcha thượng hạng cùng đậu đỏ ngọt bùi', 48000.00),

(@r64, @cat3, 'Bún Bò Huế Bắp Hoa Tái Tươi', 'Thịt bắp hoa bò bò cắt mỏng trụng tái hoa ngọt lịm', 55000.00),
(@r64, @cat3, 'Bún Bò Nạm Gân Giò Heo Lớn', 'Tô đầy đủ nạm bò ninh mềm, gân bò dai, khoanh giò heo', 60000.00),
(@r64, @cat3, 'Bún Bò Chả Cua Huyết Luộc Huế', 'Viên chả cua quét tay dẻo quánh cùng huyết heo mềm', 50000.00),
(@r64, @cat3, 'Viên Chả Cua Ngự Uyển Gọi Thêm', '2 viên chả cua đặc sản chính gốc Huế thơm ngon thêm', 16000.00),
(@r64, @cat3, 'Đĩa Bắp Chuối Rau Muống Bào Tươi', 'Rau sống bắp chuối bào sợi, giá sạch, rau húng quế', 10000.00),
(@r64, @cat3, 'Chén Nước Béo Sa Tế Ớt Siêu Cay', 'Nước béo cay nồng rưới sa tế dầu nhà làm kích thích', 5000.00),
(@r64, @cat3, 'Khoanh Giò Heo Nạc Ninh Nhừ Thêm', 'Một khúc giò heo nạc nguyên cục bản bự ăn đã miệng', 20000.00),
(@r64, @cat6, 'Hồng Trà Chanh Đá Giải Khát Ly', 'Trà đen pha chanh đường đá lạnh giải cay bún bò', 15000.00),

(@r65, @cat9, 'Pizza Thập Cẩm Cao Cấp Pizza Hut', 'Thịt bò bằm, xúc xích ngon, giăm bông, nấm, ớt chuông phủ phô mai', 239000.00),
(@r65, @cat9, 'Pizza Thịt Xông Khói Dứa Hawaii', 'Sự kết hợp thịt heo xông khói mặn và dứa mật ngọt thanh', 189000.00),
(@r65, @cat9, 'Mì Ý Hải Sản Sốt Cà Chua Đậm', 'Mì Spaghetti xào tôm, mực tươi đẫm sốt cà chua chín', 99000.00),
(@r65, @cat4, 'Cánh Gà Chiên Bơ Tỏi Nóng (4 miếng)', 'Cánh gà tẩm bột chiên xù lắc sốt bơ tỏi thơm lừng', 89000.00),
(@r65, @cat2, 'Bánh Mì Bơ Tỏi Đĩa Nhỏ Giòn', '4 lát bánh mì nướng bơ tỏi giòn tan ăn khai vị', 39000.00),
(@r65, @cat1, 'Salad Trộn Dầu Giấm Tỏi Cay', 'Xà lách tươi xóc dầu giấm tỏi ớt chua cay kích vị', 49000.00),
(@r65, @cat4, 'Khoai Tây Tây Chiên Xù Cọng Lớn', 'Khoai tây bổ múi cau chiên xù giòn bùi rắc muối', 45000.00),
(@r65, @cat6, 'Pepsi Lon 320ml Ướp Đá Mát', 'Nước ngọt giải khát ăn kèm tiệc pizza hoàn hảo', 18000.00);