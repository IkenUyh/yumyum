-- 1. Thêm nhóm tùy chọn cho món Trà Sữa Trân Châu Hoàng Kim
INSERT INTO food_option_groups (food_id, name, is_required, max_choices)
SELECT id, 'Chọn Size', TRUE, 1 FROM foods WHERE name = 'Trà Sữa Trân Châu Hoàng Kim';
SET @g_size_ts = LAST_INSERT_ID();

INSERT INTO food_option_items (group_id, name, additional_price, is_available, image_url) VALUES
(@g_size_ts, 'Size M', 0.00, TRUE, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781708443/f1d22f0f-ff9a-4a87-9ba6-a94d1dc09656.png'),
(@g_size_ts, 'Size L', 10000.00, TRUE, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781708443/f1d22f0f-ff9a-4a87-9ba6-a94d1dc09656.png');

INSERT INTO food_option_groups (food_id, name, is_required, max_choices)
SELECT id, 'Toppings Thêm', FALSE, 5 FROM foods WHERE name = 'Trà Sữa Trân Châu Hoàng Kim';
SET @g_topping_ts = LAST_INSERT_ID();

INSERT INTO food_option_items (group_id, name, additional_price, is_available, image_url) VALUES
(@g_topping_ts, 'Trân Châu Hoàng Kim', 5000.00, TRUE, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699149/4d046aff-fe44-4d08-96c6-a1c571fa2592.png'),
(@g_topping_ts, 'Sương Sáo Cắt Nhỏ', 7000.00, TRUE, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781717859/b5df1f6b-ca8e-4e4b-ad7c-92401ffd1c8f.png'),
(@g_topping_ts, 'Thạch Konjac', 8000.00, TRUE, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/v1781717940/f2319e99-0afb-47e3-aa4c-6e4b6daf0356.png');

-- 2. Thêm nhóm tùy chọn cho Bánh Mì Đặc Biệt
INSERT INTO food_option_groups (food_id, name, is_required, max_choices)
SELECT id, 'Tùy chọn Bánh Mì', FALSE, 3 FROM foods WHERE name = 'Bánh Mì Đặc Biệt';
SET @g_topping_bm = LAST_INSERT_ID();

INSERT INTO food_option_items (group_id, name, additional_price, is_available, image_url) VALUES
(@g_topping_bm, 'Pate Thêm', 5000.00, TRUE, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781699222/f8b7d150-4306-4f2f-b008-24aa9bc08962.png'),
(@g_topping_bm, 'Chả Lụa Thêm', 10000.00, TRUE, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781698921/banh-mi-pate-cha-lua_eruert.jpg'),
(@g_topping_bm, 'Thịt Xá Xíu Thêm', 12000.00, TRUE, 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1781698837/Banh-mi-huynh-hoa-topping-scaled_iqayrp.jpg');
