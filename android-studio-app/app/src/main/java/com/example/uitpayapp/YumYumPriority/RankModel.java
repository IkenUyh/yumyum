package com.example.uitpayapp.YumYumPriority;

import com.example.uitpayapp.R;
import com.example.uitpayapp.profile.MenuItemData;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class RankModel {
    public enum RankType {
        NEW("THÀNH VIÊN", "#10a941", "Hạng Thành viên", 0, Arrays.asList(
                new MenuItemData("Tích lũy Xu", "Tích 1 Xu cho mỗi 100đ", R.drawable.ic_check, false, true)
        )),
        SILVER("HẠNG BẠC", "#839bcb", "🥈 Hạng Bạc", 1000000, Arrays.asList(
                new MenuItemData("Tích lũy Xu", "Tích x1.1 Xu", R.drawable.ic_check, false, true)
        )),
        GOLD("HẠNG VÀNG", "#bfad3d", "🥇 Hạng Vàng", 5000000, Arrays.asList(
                new MenuItemData("Tích lũy Xu", "Tích x1.2 Xu", R.drawable.ic_check, false, true),
                new MenuItemData("Miễn phí vận chuyển", "Freeship tối đa 15.000đ/đơn", R.drawable.ic_check, false, true)
        )),
        DIAMOND("HẠNG KIM CƯƠNG", "#0d1e3c", "💎 Hạng Kim cương", 15000000, Arrays.asList(
                new MenuItemData("Tích lũy Xu", "Tích x1.5 Xu", R.drawable.ic_check, false, true),
                new MenuItemData("Miễn phí vận chuyển", "Freeship tối đa 30.000đ/đơn", R.drawable.ic_check, false, true)
        ));

        private final String title;
        private final String color;
        private final String badge;
        private final long threshold;
        private final List<MenuItemData> benefits;

        RankType(String title, String color, String badge, long threshold, List<MenuItemData> benefits) {
            this.title = title;
            this.color = color;
            this.badge = badge;
            this.threshold = threshold;
            this.benefits = benefits;
        }

        public String getTitle() { return title; }
        public String getColor() { return color; }
        public String getBadge() { return badge; }
        public long getThreshold() { return threshold; }
        public List<MenuItemData> getBenefits() { return benefits; }
        public int getAmountBenifits() { return benefits.size(); }

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
    private String amountVoucherBenefit;

    public RankModel(RankType rankType, long currentSpending, String benefit1) {
        this.rankType = rankType;
        this.currentSpending = currentSpending;
        this.accumulatedBenefit = benefit1;
        this.amountVoucherBenefit =rankType.getAmountBenifits()>0?"+ "+rankType.getAmountBenifits()+" Ưu đãi":"";
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
            long missing = rankType.getThreshold() - currentSpending;
            return "Thanh toán thêm " + formatter.format(missing) + "đ để lên " + rankType.getTitle();
        } else {
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
            if (rankType.getThreshold() == 0) return 100;
            return (int) Math.min(100, (currentSpending * 100) / rankType.getThreshold());
        } else {
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
    public String getVoucherBenefit() { return amountVoucherBenefit; }
    public List<MenuItemData> getRankBenefits() { return rankType.getBenefits(); }
}