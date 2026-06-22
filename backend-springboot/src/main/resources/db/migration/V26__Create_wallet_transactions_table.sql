-- Sổ cái ghi chú mọi biến động dòng tiền (Tuyệt đối không được phép có lệnh UPDATE/DELETE chạy trên bảng này)
CREATE TABLE wallet_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL, -- Số tiền biến động (Âm hoặc Dương)
    balance_after DECIMAL(19, 2) NOT NULL, -- Số dư ví NGAY SAU KHI giao dịch (Rất quan trọng để đối soát)
    type VARCHAR(50) NOT NULL, -- TOPUP (Nạp tiền), PAYMENT (Thanh toán), REFUND (Hoàn tiền), REWARD (Thưởng)
    reference_id VARCHAR(100), -- ID tham chiếu (Ví dụ: Mã đơn hàng, Mã ZaloPay)
    description TEXT NOT NULL, -- Lời diễn giải (VD: Thanh toán đơn hàng #123)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wt_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE
);