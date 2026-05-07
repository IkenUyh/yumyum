package com.example.uitpayapp.UITpayPriority;

import java.text.DecimalFormat;

public class RankModel {
public enum RankType {
        NEW("TÀI KHOẢN MỚI", "#10a941", "Hạng Thành viên", 0),
        SILVER("HẠNG BẠC", "#839bcb", "🥈 Hạng Bạc", 3000000),
        GOLD("HẠNG VÀNG", "#bfad3d", "🥇 Hạng Vàng", 18000000),
        DIAMOND("HẠNG KIM CƯƠNG", "#0d1e3c", "💎 Hạng Kim cương", 60000000);

        private final String title;
        private final String color;
        private final String badge;
        private final long threshold;

        RankType(String title, String color, String badge, long threshold) {
            this.title = title;
            this.color = color;
            this.badge = badge;
            this.threshold = threshold;
        }

        public String getTitle() { return title; }
        public String getColor() { return color; }
        public String getBadge() { return badge; }
        public long getThreshold() { return threshold; }

        public RankType getNext() {
            RankType[] values = RankType.values();
            int nextOrdinal = this.ordinal() + 1;
            if (nextOrdinal < values.length) {
                return values[nextOrdinal];
            }
            return null;
        }
    }

    private RankType rankType;
    private long currentSpending;
    private String accumulatedBenefit;
    private String voucherBenefit;

    public RankModel(RankType rankType, long currentSpending, String benefit1, String benefit2) {
        this.rankType = rankType;
        this.currentSpending = currentSpending;
        this.accumulatedBenefit = benefit1;
        this.voucherBenefit = benefit2;
    }

    public boolean isLocked() {
        return currentSpending < rankType.getThreshold();
    }

    public String getRankTitle() {
        return isLocked() ? rankType.getTitle() + " ĐÃ KHOÁ" : rankType.getTitle();
    }

    public String getCondition() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (isLocked()) {
            // Trường hợp hạng đang xem bị khoá
            long missing = rankType.getThreshold() - currentSpending;
            return "Thanh toán thêm " + formatter.format(missing) + "đ để lên " + rankType.getTitle();
        } else {
            // Trường hợp đã đạt hạng này, hiển thị tiến trình lên hạng tiếp theo
            RankType next = rankType.getNext();
            if (next != null) {
                long missing = next.getThreshold() - currentSpending;
                if (missing > 0) {
                    return "Thanh toán thêm " + formatter.format(missing) + "đ để thăng hạng " + next.getTitle();
                }
            }
            return "Bạn đã đạt hạng cao nhất!";
        }
    }

    public int getProgress() {
        if (isLocked()) {
            // Nếu bị khóa, progress dựa trên chính mốc của hạng đó
            if (rankType.getThreshold() == 0) return 100;
            return (int) Math.min(100, (currentSpending * 100) / rankType.getThreshold());
        } else {
            // Nếu đã đạt hạng, progress dựa trên hạng tiếp theo
            RankType next = rankType.getNext();
            if (next != null) {
                return (int) Math.min(100, (currentSpending * 100) / next.getThreshold());
            }
            return 100;
        }
    }

    public RankType getRankType() { return rankType; }
    public String getRankBadge() { return rankType.getBadge(); }
    public String getAccumulatedBenefit() { return accumulatedBenefit; }
    public String getVoucherBenefit() { return voucherBenefit; }
}
