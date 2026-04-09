package com.example.uitpayapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    RecyclerView mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        TextView pagetitle = findViewById(R.id.pagetilte);
        mainMenu = findViewById(R.id.main_menu);
        LinearLayout profile_container = findViewById(R.id.profile_container);
        ViewCompat.setOnApplyWindowInsetsListener(pagetitle, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom+10;
            if (profile_container != null) {
                profile_container.setPadding(profile_container.getPaddingLeft(), profile_container.getPaddingTop(), profile_container.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        SetDataMainMenu(mainMenu);
    }

    void SetDataMainMenu(RecyclerView mainMenu) {
        if (mainMenu == null) return;
        mainMenu.setLayoutManager(new LinearLayoutManager(this));
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        //Nhóm 1: Ưu đãi
        List<MenuItemData> ListItems_uudai = new ArrayList<>();
        ListItems_uudai.add(new MenuItemData("Quà của tôi", "0 ưu đãi", R.drawable.ic_my_gift,false));
        ListItems_uudai.add(new MenuItemData("Xu tích lũy", "0 xu", R.drawable.ic_my_coin,false));
        ListGroupItem.add(new GroupItemData("Ưu đãi", ListItems_uudai));
        //Nhóm 2: Quản lý tài chính (Mục đặc chứa thành phần đặc biệt)
        List<MenuItemData> ListItems_finance = new ArrayList<>();
        ListItems_finance.add(new MenuItemData("Tài khoản/thẻ liên kết", "", R.drawable.ic_account_card_payment,true));
        ListItems_finance.add(new MenuItemData("Cài đặt thanh toán tự động", "Sắp xếp nguồn tiền, cài đặt dịch vụ", R.drawable.ic_payment,false));
        ListItems_finance.add(new MenuItemData("Điểm tin cậy UITpay", "", R.drawable.ic_reliable_score,false));
        ListGroupItem.add(new GroupItemData("Quản lý tài chính", ListItems_finance));
        //Nhóm 3: Tiện ích
        List<MenuItemData> ListItems_tienich = new ArrayList<>();
        ListItems_tienich.add(new MenuItemData("Quản lý hóa đơn", "Thêm hóa đơn để thanh toán bạn nhé", R.drawable.ic_receipt,false));
        ListItems_tienich.add(new MenuItemData("Quản lý hợp đồng", "", R.drawable.ic_contract,false));
        ListItems_tienich.add(new MenuItemData("Quản lý vé", "", R.drawable.ic_ticket,false));
        ListGroupItem.add(new GroupItemData("Tiện ích", ListItems_tienich));
        //Nhóm 4: Hỗ trợ
        List<MenuItemData> ListItems_support = new ArrayList<>();
        ListItems_support.add(new MenuItemData("Trung tâm hỗ trợ", "", R.drawable.ic_contact_support_ver2,false));
        ListItems_support.add(new MenuItemData("Trung tâm bảo mật", "", R.drawable.ic_security_user,false));
        ListItems_support.add(new MenuItemData("Cài đặt ứng dụng", "", R.drawable.ic_setting,false));
        ListGroupItem.add(new GroupItemData("Hỗ trợ và Cài đặt", ListItems_support));

        mainMenu.setAdapter(new ProfileMenuAdapter(this, ListGroupItem,ItemClick->HanleItemClick(ItemClick)));

        findViewById(R.id.qr_manage_card).setOnClickListener(v->
        {
            List<MenuItemData> ListItems_QRManger = new ArrayList<>();
            List<GroupItemData> ListGroupItemQRManger = new ArrayList<>();
            ListItems_QRManger.add(new MenuItemData("Mã nhận tiền","Chia sẻ mã này để nhận tiền những người xung quanh",R.drawable.ic_receive,false));
            ListItems_QRManger.add(new MenuItemData("Mã thanh toán","Đưa mã này cho thu ngân cửa hàng để thanh toán nhé",R.drawable.ic_qr_code,false));
            ListItems_QRManger.add(new MenuItemData("Quét QR","Hướng camera vào mã QR để quyét",R.drawable.ic_scan,false));
            ListGroupItemQRManger.add(new GroupItemData("Các mã QR quan trọng",ListItems_QRManger));
            ShowBottomSheet("Quản lý mã",ListGroupItemQRManger);
        });

    }
    public void HanleItemClick(MenuItemData item) {
        if (item == null) return;
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        List<MenuItemData> ListItems = new ArrayList<>();
        switch (item.title) {
            case "Cài đặt thanh toán tự động":
                ListItems.add(new MenuItemData("Thanh toán dịch vụ tự động","Sắp xếp thứ tự ưu tiên thẻ/tài khoản",R.drawable.ic_sort_payment,false));
                ListItems.add(new MenuItemData("Thanh toán hóa đơn tự động","",R.drawable.ic_receipt,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Cài đặt thanh toán tự động",ListGroupItem);
                break;
            case "Quản lý hợp đồng":
                ListItems.add(new MenuItemData("Hợp đồng bảo hiểm","",R.drawable.ic_contract_insurance,false));
                ListItems.add(new MenuItemData("Hợp đồng số dư sinh lời","",R.drawable.ic_accmulated_balance,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Quản lý hợp đồng",ListGroupItem);
                break;
            case "Quản lý vé":
                ListItems.add(new MenuItemData("Vé xem phim","",R.drawable.ic_movie_ticket,false));
                ListItems.add(new MenuItemData("Vé máy bay","",R.drawable.ic_flight_ticket,false));
                ListItems.add(new MenuItemData("Vé xe khách","",R.drawable.ic_bus_ticket,false));
                ListItems.add(new MenuItemData("Vé tàu hỏa","",R.drawable.ic_train_ticket,false));
                ListItems.add(new MenuItemData("Vé tham quan","",R.drawable.ic_tourist_attraction_ticket,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Quản lý vé",ListGroupItem);
                break;
            case "Trung tâm bảo mật":
                ListItems.add(new MenuItemData("Bảo mật tài khoản","",R.drawable.ic_security_user,false));
                ListItems.add(new MenuItemData("Bảo mật giao dịch","",R.drawable.ic_security_transaction,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Trung tâm bảo mật",ListGroupItem);
                break;
            case "Cài đặt ứng dụng":
                ListItems.add(new MenuItemData("Cài đặt thông báo","",R.drawable.ic_notification,false));
                ListGroupItem.add(new GroupItemData("Cài đặt cho ứng dụng UITpay",ListItems));
                List<MenuItemData> ListItems2 = new ArrayList<>();
                ListItems2.add(new MenuItemData("Thông tin ứng dụng","",R.drawable.ic_uitpay_information,false));
                ListItems2.add(new MenuItemData("Dọn dẹp bộ nhớ tạm","",R.drawable.ic_clean,false));
                ListGroupItem.add(new GroupItemData("Khác",ListItems2));
                ShowBottomSheet("Cài đặt ứng dụng",ListGroupItem);
                break;
            case "Quản lý tài chính":
                ListItems.add(new MenuItemData("Tài khoản/thẻ liên kết","",R.drawable.ic_account_card_payment,false));
                ListItems.add(new MenuItemData("Cài đặt thanh toán tự động","Sắp xếp nguồn tiền, cài đặt dịch vụ",R.drawable.ic_payment,false));
                ListItems.add(new MenuItemData("Điểm tin cậy UITpay","",R.drawable.ic_reliable_score,false));
                ListItems.add(new MenuItemData("các liên kết ngân hàng khác","",R.drawable.ic_link_bank,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Quản lý tài chính",ListGroupItem);
        }
    }
    public static void SetDetaileMenuItem(View item, String item_title, String item_subtitle, int item_icon) {
        if (item == null) return;
        TextView titleTv = item.findViewById(R.id.menu_title);
        TextView subtitleTv = item.findViewById(R.id.menu_subtitle);
        ImageView iconIv = item.findViewById(R.id.menu_icon);

        if (titleTv != null) titleTv.setText(item_title);
        if (subtitleTv != null) {
            if (item_subtitle != null && !item_subtitle.isEmpty()) {
                subtitleTv.setVisibility(View.VISIBLE);
                subtitleTv.setText(item_subtitle);
            } else {
                subtitleTv.setVisibility(View.GONE);
            }
        }
        if (iconIv != null) iconIv.setImageResource(item_icon);
    }

    public static class MenuItemData {
        private String title, subtitle;
        private int icon;
        Boolean IsSpecialItem;
        public MenuItemData(String title, String subtitle, int icon, Boolean IsSpecialItem) {
            this.title = title; this.subtitle = subtitle; this.icon = icon;
            this.IsSpecialItem = IsSpecialItem;
        }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
        public int getIcon() { return icon; }
    }

    public static class GroupItemData {
        private String title;
        private List<MenuItemData> ListItems;
        public GroupItemData(String title, List<MenuItemData> ListItems) {
            this.title = title; this.ListItems = ListItems;
        }
        public String getTitle() { return title; }
        public List<MenuItemData> getListItems() { return ListItems; }
    }
    //Tam thoi code thu cong do du lieu con it
    private void ShowBottomSheet(String SheetTilte, List<GroupItemData> ListGroupItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View SheetView=getLayoutInflater().inflate(R.layout.layout_dynamic_bottom_sheet,null);
        ((ImageView)SheetView.findViewById(R.id.btn_close)).setOnClickListener(v->bottomSheetDialog.dismiss());
        ((TextView)SheetView.findViewById(R.id.sheet_title)).setText(SheetTilte);
        RecyclerView sheet_container=SheetView.findViewById(R.id.sheet_container);
        sheet_container.setLayoutManager(new LinearLayoutManager(this));
        sheet_container.setAdapter(new ProfileMenuAdapter(this,ListGroupItem,null));
        //TAM THOI DE LISTENER LA NULL, TRONG TUONG LAI LAM TIEP SE PHAN LOAI THEM 1 FUNCTION NUA
        bottomSheetDialog.setContentView(SheetView);
        bottomSheetDialog.show();
    }
}
