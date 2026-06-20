-- V38__Insert_active_flashsale.sql
-- Thêm chiến dịch Flashsale hoạt động cho các món ăn mẫu của quán Cơm Tấm Phúc Lộc Thọ

INSERT INTO flash_sales (name, start_time, end_time, is_active)
VALUES ('Siêu Sale 50%', '2026-01-01 00:00:00', '2030-12-31 23:59:59', TRUE);
SET @fs_id = LAST_INSERT_ID();

SET @res_com = (SELECT id FROM restaurants WHERE name = 'Cơm Tấm Phúc Lộc Thọ - Đinh Tiên Hoàng');
SET @food_surn = (SELECT id FROM foods WHERE name = 'Cơm Sườn Nướng Mật Ong' AND restaurant_id = @res_com);
SET @food_surn_bi = (SELECT id FROM foods WHERE name = 'Cơm Sườn Bì Chả' AND restaurant_id = @res_com);
SET @food_ba_roi = (SELECT id FROM foods WHERE name = 'Cơm Ba Rọi Nướng Sả' AND restaurant_id = @res_com);

-- Thêm các món ăn vào chi tiết Flashsale với mức giá giảm 50%
INSERT INTO flash_sale_items (flash_sale_id, food_id, sale_price, stock_quantity, sold_quantity, version)
VALUES 
(@fs_id, @food_surn, 21000.00, 100, 0, 0),
(@fs_id, @food_surn_bi, 27500.00, 100, 0, 0),
(@fs_id, @food_ba_roi, 22500.00, 100, 0, 0);
