ALTER TABLE orders
    ADD COLUMN payment_method VARCHAR(50) DEFAULT 'WALLET',
    ADD COLUMN payment_status VARCHAR(30) DEFAULT 'PAID',
    ADD COLUMN zalopay_app_trans_id VARCHAR(100) NULL,
    ADD COLUMN zalopay_zp_trans_id VARCHAR(100) NULL;
