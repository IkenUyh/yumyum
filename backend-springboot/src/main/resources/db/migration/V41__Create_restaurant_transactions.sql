CREATE TABLE restaurant_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    balance_after DECIMAL(19, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    reference_id VARCHAR(100),
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rest_tx_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- Khôi phục lịch sử giao dịch (Sổ cái) từ các đơn hàng COMPLETED cũ
INSERT INTO restaurant_transactions (restaurant_id, amount, balance_after, type, reference_id, description, created_at)
SELECT 
    o.restaurant_id,
    (o.total_amount - COALESCE(o.shipping_fee, 0)) AS amount,
    SUM(o.total_amount - COALESCE(o.shipping_fee, 0)) OVER (PARTITION BY o.restaurant_id ORDER BY o.created_at) AS balance_after,
    'REVENUE' AS type,
    CONCAT('ORDER_', o.id) AS reference_id,
    CONCAT('Doanh thu bán hàng đơn #', o.id) AS description,
    o.created_at
FROM orders o
WHERE o.status = 'COMPLETED';
