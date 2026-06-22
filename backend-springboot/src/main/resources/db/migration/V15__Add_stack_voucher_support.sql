-- 1. Tạo bảng trung gian để lưu vết nhiều Voucher cho một Đơn hàng (Quan hệ Nhiều - Nhiều)
CREATE TABLE order_vouchers (
    order_id BIGINT NOT NULL,
    voucher_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, voucher_id),
    CONSTRAINT fk_ov_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_ov_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id) ON DELETE CASCADE
);

ALTER TABLE orders DROP FOREIGN KEY fk_order_voucher;
ALTER TABLE orders DROP COLUMN voucher_id;