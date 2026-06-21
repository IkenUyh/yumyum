-- Đồng bộ hóa lại điểm đánh giá và số lượng đánh giá cho các cửa hàng 
-- dựa trên dữ liệu thực tế đang tồn tại trong bảng reviews.
UPDATE restaurants r
SET 
    review_count = (SELECT COUNT(*) FROM reviews rev WHERE rev.restaurant_id = r.id),
    rating_average = COALESCE((SELECT ROUND(AVG(rating), 1) FROM reviews rev WHERE rev.restaurant_id = r.id), 0.0);
