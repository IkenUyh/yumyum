-- Thêm cờ trạng thái hoạt động cho User
ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Cập nhật data cũ cho chắc chắn không bị lỗi Null
UPDATE users SET is_active = TRUE WHERE is_active IS NULL;