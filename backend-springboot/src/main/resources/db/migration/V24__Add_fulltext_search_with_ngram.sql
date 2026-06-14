-- Tạo chỉ mục Full-Text Search kết hợp bộ băm Ngram cho cột name của quán ăn và món ăn
-- Giúp hệ thống tìm kiếm mờ (Fuzzy Search) và chịu lỗi gõ sai chính tả (Typo Tolerance)
ALTER TABLE restaurants ADD FULLTEXT INDEX ft_idx_res_name (name) WITH PARSER ngram;
ALTER TABLE foods ADD FULLTEXT INDEX ft_idx_food_name (name) WITH PARSER ngram;