ALTER TABLE restaurants
ADD COLUMN balance DECIMAL(19, 2) DEFAULT 0.00;

-- Khôi phục số dư cho cửa hàng dựa trên các đơn hàng COMPLETED trong quá khứ
UPDATE restaurants r
SET balance = (
    SELECT COALESCE(SUM(o.total_amount - COALESCE(o.shipping_fee, 0)), 0)
    FROM orders o
    WHERE o.restaurant_id = r.id AND o.status = 'COMPLETED'
);
