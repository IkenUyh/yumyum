package com.example.uitpayapp.YumYumPriority;

import com.example.uitpayapp.R;
import com.example.uitpayapp.profile.MenuItemData;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class RankModel {
    public enum RankType {
        NEW("THÀNH VIÊN", "#10a941", "Hạng Thành viên", 0, Arrays.asList(
                new MenuItemData("Mã miễn phí vận chuyển hàng tháng", "Nhận 01 mã mỗi tháng", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Ngày hội Thành viên", "9H ngày 10 & 20 hàng tháng", R.drawable.ic_check, false)
        )),
        SILVER("HẠNG BẠC", "#839bcb", "🥈 Hạng Bạc", 1000000, Arrays.asList(
                new MenuItemData("Mã miễn phí vận chuyển hàng tháng", "Nhận 01 mã mỗi tháng", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Ngày hội Thành viên", "9H ngày 10 & 20 hàng tháng", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Độc quyền từ Thương hiệu và Đối tác", "Dành riêng cho hạng Bạc", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Độc quyền từ Người bán", "Ưu đãi từ người bán trên Shopee", R.drawable.ic_check, false),
                new MenuItemData("Voucher HOT từ Shop", "Nhiều mã giảm giá hấp dẫn", R.drawable.ic_check, false)
        )),
        GOLD("HẠNG VÀNG", "#bfad3d", "🥇 Hạng Vàng", 5000000, Arrays.asList(
                new MenuItemData("Mã miễn phí vận chuyển hàng tháng", "Nhận 01 mã mỗi tháng", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Ngày hội Thành viên", "9H ngày 10 & 20 hàng tháng", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Độc quyền từ Thương hiệu và Đối tác", "Ưu tiên cho hạng Vàng", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Độc quyền từ Người bán", "Ưu đãi từ người bán trên Shopee", R.drawable.ic_check, false),
                new MenuItemData("Voucher HOT từ Shop", "Mã giảm giá độc quyền", R.drawable.ic_check, false),
                new MenuItemData("Voucher thăng hạng", "Quà tặng khi đạt hạng Vàng", R.drawable.ic_check, false),
                new MenuItemData("Voucher sinh nhật", "Quà tặng đặc biệt ngày sinh nhật", R.drawable.ic_check, false)
        )),
        DIAMOND("HẠNG KIM CƯƠNG", "#0d1e3c", "💎 Hạng Kim cương", 15000000, Arrays.asList(
                new MenuItemData("02 Mã miễn phí Vận chuyển", "Tặng 02 mã mỗi tháng", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Ngày hội Thành viên", "9H ngày 10 & 20 hàng tháng", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Độc quyền từ Thương hiệu và Đối tác", "Đặc quyền cao cấp nhất", R.drawable.ic_check, false),
                new MenuItemData("Ưu đãi Độc quyền từ Người bán", "Ưu đãi từ người bán trên Shopee", R.drawable.ic_check, false),
                new MenuItemData("Voucher HOT từ Shop", "Mã giảm giá VIP", R.drawable.ic_check, false),
                new MenuItemData("Voucher thăng hạng", "Quà tặng khi đạt hạng Kim cương", R.drawable.ic_check, false),
                new MenuItemData("Voucher sinh nhật", "Quà tặng đặc biệt ngày sinh nhật", R.drawable.ic_check, false),
                new MenuItemData("Voucher duy trì thứ hạng", "Dành riêng cho Kim Cương", R.drawable.ic_check, false)
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
