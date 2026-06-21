-- V63__Remove_duplicate_reviews.sql
-- Xóa các đánh giá bị trùng lặp order_id để fix lỗi NonUniqueResultException
-- Lệnh này sẽ quét toàn bộ bảng reviews, nếu 1 đơn hàng có nhiều đánh giá thì nó sẽ giữ lại đánh giá mới nhất (ID lớn nhất) và xóa các đánh giá cũ đi.

DELETE r1 
FROM reviews r1
INNER JOIN reviews r2 
WHERE r1.order_id = r2.order_id 
  AND r1.id < r2.id;
