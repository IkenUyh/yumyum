package com.example.uitpayapp.YumYumPriority;

import com.example.uitpayapp.R;
import com.example.uitpayapp.profile.MenuItemData;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class RankModel {
    public enum RankType {
        NEW("TÀI KHOẢN MỚI", "#10a941", "Hạng Thành viên", 0, Arrays.asList(
                new MenuItemData("Giảm giá từ Xu", "Có 1 Xu, giảm 1đ", R.drawable.ic_my_coin, false),
                new MenuItemData("Nhận ngay 1 Xu", "Với mỗi 5.000đ thanh toán", R.drawable.ic_qr, false),
                new MenuItemData("Nhận x2 Xu khi thanh toán", "Bằng Số Dư Sinh Lời hoặc Tài Khoản Trả Sau", R.drawable.ic_accmulated_balance, false),
                new MenuItemData("Dùng Xu đổi voucher hot", "Voucher giảm đến 50% siêu hot", R.drawable.ic_ticket, false),
                new MenuItemData("Deal Flashsale Thứ 4 hằng tuần", "Voucher xịn giảm đến 100K", R.drawable.ic_fire, false)
        )),
        SILVER("HẠNG BẠC", "#839bcb", "🥈 Hạng Bạc", 3000000, Arrays.asList(
                new MenuItemData("Tặng thêm 0.1% mức sinh lời", "Khi dùng Số Dư Sinh Lời", R.drawable.ic_accmulated_balance, false),
                new MenuItemData("Quà tặng lên hạng", "Nhận 1000 Xu khi lên hạng Bạc", R.drawable.ic_priority_account_rank, false),
                new MenuItemData("Quà sinh nhật", "Quà sẽ được bật mí vào ngày sinh nhật", R.drawable.ic_gift, false),
                new MenuItemData("Dùng Xu đổi voucher hot", "Nhiều deal xịn dành riêng hạng Bạc", R.drawable.ic_ticket, false),
                new MenuItemData("Nhận ngay 1 Xu", "Với mỗi 5.000đ thanh toán", R.drawable.ic_qr, false),
                new MenuItemData("Nhận x2 Xu khi thanh toán", "Bằng Số Dư Sinh Lời hoặc Tài Khoản Trả Sau", R.drawable.ic_accmulated_balance, false),
                new MenuItemData("Giảm giá từ Xu", "Không cần voucher vẫn giảm giá", R.drawable.ic_my_coin, false),
                new MenuItemData("Deal Flashsale Thứ 4 hằng tuần", "Voucher xịn giảm đến 100K", R.drawable.ic_fire, false)
        )),
        GOLD("HẠNG VÀNG", "#bfad3d", "🥇 Hạng Vàng", 18000000, Arrays.asList(
                new MenuItemData("Tặng thêm 0.3% mức sinh lời", "Khi dùng Số Dư Sinh Lời", R.drawable.ic_accmulated_balance, false),
                new MenuItemData("Tặng thêm 0.1% số tiền", "Khi đáo hạn gói Gửi Tiết Kiệm 6 tháng", R.drawable.ic_wallet, false),
                new MenuItemData("Giảm 50% phí dịch vụ hằng tháng", "Khi sử dụng Tài Khoản Trả Sau", R.drawable.ic_menu_autopay, false),
                new MenuItemData("Hoàn phí mua/bán Chứng khoán", "Tối đa 15K cho Hội viên Vàng", R.drawable.ic_chart, false),
                new MenuItemData("Quà sinh nhật", "Quà sẽ được bật mí vào ngày sinh nhật", R.drawable.ic_gift, false),
                new MenuItemData("Quà tặng lên hạng", "Nhận 3000 Xu khi lên hạng Vàng", R.drawable.ic_priority_account_rank, false),
                new MenuItemData("Dùng Xu đổi voucher hot", "Nhiều voucher giảm 50% cho hạng Vàng", R.drawable.ic_ticket, false),
                new MenuItemData("Nhận ngay 1.2 Xu", "Với mỗi 5.000đ thanh toán", R.drawable.ic_qr, false),
                new MenuItemData("Nhận x2.4 Xu khi thanh toán", "Bằng Số Dư Sinh Lời hoặc Tài Khoản Trả Sau", R.drawable.ic_accmulated_balance, false),
                new MenuItemData("Giảm giá từ Xu", "Không cần voucher vẫn giảm giá", R.drawable.ic_my_coin, false),
                new MenuItemData("Deal Flashsale Thứ 4 hằng tuần", "Voucher xịn giảm đến 100K", R.drawable.ic_fire, false)
        )),
        DIAMOND("HẠNG KIM CƯƠNG", "#0d1e3c", "💎 Hạng Kim cương", 60000000, Arrays.asList(
                new MenuItemData("Tặng thêm 0.5% mức sinh lời", "Khi dùng Số Dư Sinh Lời", R.drawable.ic_accmulated_balance, false),
                new MenuItemData("Tặng thêm 0.3% số tiền", "Khi đáo hạn gói Gửi Tiết Kiệm 6 tháng", R.drawable.ic_wallet, false),
                new MenuItemData("Miễn phí phí dịch vụ hằng tháng", "Khi sử dụng Tài Khoản Trả Sau", R.drawable.ic_menu_autopay, false),
                new MenuItemData("Lối vượt ưu tiên", "Dành riêng Hội viên Kim Cương", R.drawable.ic_priority_account_rank, false),
                new MenuItemData("Hoàn phí mua/bán Chứng khoán", "Tối đa 25K cho Hội viên Kim Cương", R.drawable.ic_chart, false),
                new MenuItemData("Hotline ưu tiên", "Hỗ trợ nhanh chóng", R.drawable.ic_contact_sp, false),
                new MenuItemData("Quà sinh nhật", "Quà sẽ được bật mí vào ngày sinh nhật", R.drawable.ic_gift, false),
                new MenuItemData("Quà tặng lên hạng", "Nhận 5000 Xu khi lên hạng Kim Cương", R.drawable.ic_priority_account_rank, false),
                new MenuItemData("Quà đặc biệt", "Dành riêng Hội viên Kim Cương", R.drawable.ic_priority_account_rank, false),
                new MenuItemData("Dùng Xu đổi voucher hot", "+1000 voucher xịn cho hạng Kim Cương", R.drawable.ic_ticket, false),
                new MenuItemData("Nhận ngay 1.5 Xu", "Với mỗi 5.000đ thanh toán", R.drawable.ic_qr, false),
                new MenuItemData("Nhận x3 Xu khi thanh toán", "Bằng Số Dư Sinh Lời hoặc Tài Khoản Trả Sau", R.drawable.ic_accmulated_balance, false),
                new MenuItemData("Giảm giá từ Xu", "Không cần voucher vẫn giảm giá", R.drawable.ic_my_coin, false),
                new MenuItemData("Deal Flashsale Thứ 4 hằng tuần", "Voucher xịn giảm đến 100K", R.drawable.ic_fire, false)
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
    public String getVoucherBenefit() { return voucherBenefit; }
    public List<MenuItemData> getRankBenefits() { return rankType.getBenefits(); }
}
