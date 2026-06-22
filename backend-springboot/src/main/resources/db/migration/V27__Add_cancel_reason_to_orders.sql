-- Thêm cột Lý do hủy đơn để lưu vết tra soát
ALTER TABLE orders ADD COLUMN cancel_reason VARCHAR(255);