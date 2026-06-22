-- V34__Add_Review_Coin_Param.sql
-- Thêm tham số cấu hình phần thưởng xu khi đánh giá nhà hàng
INSERT INTO system_parameters (param_key, param_value, description) VALUES
    ('REVIEW_COIN_REWARD', '500', 'Số xu nhận được sau khi gửi đánh giá đơn hàng thành công');
