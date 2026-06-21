-- V60__Insert_sample_orders.sql
-- Diverse order generation for stores 1 to 10

SET @u1 = (SELECT id FROM users WHERE phone_number = '0983123456' LIMIT 1);
SET @u2 = (SELECT id FROM users WHERE phone_number = '0974567890' LIMIT 1);
SET @u3 = (SELECT id FROM users WHERE phone_number = '0965987654' LIMIT 1);
SET @u4 = (SELECT id FROM users WHERE phone_number = '0868112233' LIMIT 1);
SET @u5 = (SELECT id FROM users WHERE phone_number = '0327445566' LIMIT 1);

-- ==========================================
-- Store 1
-- ==========================================
SET @res_id = 1;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 1, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 2, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 2 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 3, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 5 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 4, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 12 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 5, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 24 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;

-- ==========================================
-- Store 2
-- ==========================================
SET @res_id = 2;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 2, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 3, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 2 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 4, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 4 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 5, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 14 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 1, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 28 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;


-- ==========================================
-- Store 3
-- ==========================================
SET @res_id = 3;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 3, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 4, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 3 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 5, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 6 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 1, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 15 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 2, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 30 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;


-- ==========================================
-- Store 4
-- ==========================================
SET @res_id = 4;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 4, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 5, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 2 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 1, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 7 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 2, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 16 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 3, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 36 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;


-- ==========================================
-- Store 5
-- ==========================================
SET @res_id = 5;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 5, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 1, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 2 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 2, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 8 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 3, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 18 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 4, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 48 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;


-- ==========================================
-- Store 6
-- ==========================================
SET @res_id = 6;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 1, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 3, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 2 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 5, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 9 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 2, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 24 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 4, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 72 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;


-- ==========================================
-- Store 7
-- ==========================================
SET @res_id = 7;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 2, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 4, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 2 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 1, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 10 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 5, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 36 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 3, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 96 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;


-- ==========================================
-- Store 8
-- ==========================================
SET @res_id = 8;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 3, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 5, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 3 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 2, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 11 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 4, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 48 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 1, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 120 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;


-- ==========================================
-- Store 9
-- ==========================================
SET @res_id = 9;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 4, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 1, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 3 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 5, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 12 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 3, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 60 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 2, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 144 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;


-- ==========================================
-- Store 10
-- ==========================================
SET @res_id = 10;
SET @f1 = (SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 0);
SET @p1 = COALESCE((SELECT price FROM foods WHERE id = @f1), 35000);
SET @f2 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 1), @f1);
SET @p2 = COALESCE((SELECT price FROM foods WHERE id = @f2), @p1);
SET @f3 = COALESCE((SELECT id FROM foods WHERE restaurant_id = @res_id LIMIT 1 OFFSET 2), @f1);
SET @p3 = COALESCE((SELECT price FROM foods WHERE id = @f3), @p1);

-- Order 1: PENDING, User 5, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u5, @res_id, (@p1 * 1) + (@p2 * 2) + 15000, 'PENDING', 15000, DATE_SUB(NOW(), INTERVAL 1 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 2, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;

-- Order 2: PREPARING, User 2, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u2, @res_id, (@p3 * 2) + 15000, 'PREPARING', 15000, DATE_SUB(NOW(), INTERVAL 4 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 2, @p3 FROM DUAL WHERE @f3 IS NOT NULL;

-- Order 3: DELIVERING, User 4, 2 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u4, @res_id, (@p1 * 3) + (@p3 * 1) + 15000, 'DELIVERING', 15000, DATE_SUB(NOW(), INTERVAL 14 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 3, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1;

-- Order 4: COMPLETED, User 1, 1 item
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u1, @res_id, (@p2 * 1) + 15000, 'COMPLETED', 15000, DATE_SUB(NOW(), INTERVAL 72 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL;

-- Order 5: CANCELLED, User 3, 3 items
INSERT INTO orders (user_id, restaurant_id, total_amount, status, shipping_fee, created_at) VALUES (@u3, @res_id, (@p1 * 1) + (@p2 * 1) + (@p3 * 1) + 15000, 'CANCELLED', 15000, DATE_SUB(NOW(), INTERVAL 168 HOUR));
SET @oid = LAST_INSERT_ID();
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f1, 1, @p1 FROM DUAL WHERE @f1 IS NOT NULL;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f2, 1, @p2 FROM DUAL WHERE @f2 IS NOT NULL AND @f2 != @f1;
INSERT INTO order_items (order_id, food_id, quantity, price) SELECT @oid, @f3, 1, @p3 FROM DUAL WHERE @f3 IS NOT NULL AND @f3 != @f1 AND @f3 != @f2;

