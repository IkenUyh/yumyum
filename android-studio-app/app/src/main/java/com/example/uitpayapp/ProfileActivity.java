package com.example.uitpayapp;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    Boolean IsAmountHidden = true;
    TextView walletBalance;
    TextView accmulatedBalance;
    ImageView hideShowAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        TextView pagetitle = findViewById(R.id.pagetilte);
        LinearLayout mainmenu = findViewById(R.id.main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(pagetitle, (v, insets) -> {
            //diem khuyet camera
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top+10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            mainmenu.setPadding(mainmenu.getPaddingLeft(), mainmenu.getPaddingTop(), mainmenu.getPaddingRight(), safeBottomPadding);
            return insets;
        });
        //Qua cua toi
        SetLayoutMenuItem(R.id.my_gift_item,"Quà của tôi","0 ưu đãi",R.drawable.ic_my_gift);
        //Xu tich luy
        SetLayoutMenuItem(R.id.my_coin_item,"Xu tích lũy","0 xu",R.drawable.ic_my_coin);
        //Tai khoan/the lien ket
        SetLayoutMenuItem(R.id.account_card_payment,"Tài khoản/thẻ liên kết","",R.drawable.ic_account_card_payment);
        //Cai dat thanh toan tu dong
        SetLayoutMenuItem(R.id.auto_payment_setting_item,"Cài đặt thanh toán tự động","Sắp xếp nguồn tiền, cài đặt dịch vụ",R.drawable.ic_payment);
        //Diem tin cay
        SetLayoutMenuItem(R.id.reliability_score_item,"Điểm tin cậy UITpay","",R.drawable.ic_reliable_score);
        //Quan ly hoa don
        SetLayoutMenuItem(R.id.my_receipt_item,"Quản lý hóa đơn","Thêm hóa đơn để thanh toán bạn nhé",R.drawable.ic_receipt);
        //Quan ly hop dong
        SetLayoutMenuItem(R.id.my_contract_item,"Quản lý hợp đồng","",R.drawable.ic_contract);
        //Quan ly ve
        SetLayoutMenuItem(R.id.my_ticket_item,"Quản lý vé","",R.drawable.ic_ticket);
        //Trung tam ho tro
        SetLayoutMenuItem(R.id.contact_support_item,"Trung tâm hỗ trợ","",R.drawable.ic_contact_support_ver2);
        //Trung tam bao mat
        SetLayoutMenuItem(R.id.security_center_item,"Trung tâm bảo mật","",R.drawable.ic_security_user);
        //Cai dat ung dung
        SetLayoutMenuItem(R.id.setting_item,"Cài đặt ứng dụng","",R.drawable.ic_setting);

        walletBalance = findViewById(R.id.wallet_balance);
        accmulatedBalance = findViewById(R.id.accmulated_balance);
        hideShowAmount = findViewById(R.id.hide_show_amount);
        hideShowAmount.setOnClickListener(v->
        {
            IsAmountHidden = !IsAmountHidden;
            if (!IsAmountHidden)
            {
                walletBalance.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                accmulatedBalance.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hideShowAmount.setImageResource(R.drawable.ic_invisible_eye);
            } else
            {
                walletBalance.setTransformationMethod(PasswordTransformationMethod.getInstance());
                accmulatedBalance.setTransformationMethod(PasswordTransformationMethod.getInstance());
                hideShowAmount.setImageResource(R.drawable.ic_eye);
            }
        });
        findViewById(R.id.auto_payment_setting_item).setOnClickListener(v->{
            List<GroupItemData> ListGroupItem = new ArrayList<>();
            List<MenuItemData> ListItems = new ArrayList<>();
            ListItems.add(new MenuItemData("Thanh toán dịch vụ tự động","Sắp xếp thứ tự ưu tiên thẻ/tài khoản",R.drawable.ic_sort_payment));
            ListItems.add(new MenuItemData("Thanh toán hóa đơn tự động","",R.drawable.ic_receipt));
            ListGroupItem.add(new GroupItemData("",ListItems));
            ShowBottomSheet("Cài đặt thanh toán tự động",ListGroupItem);
        });
        findViewById(R.id.my_contract_item).setOnClickListener(v-> {
            List<GroupItemData> ListGroupItem = new ArrayList<>();
            List<MenuItemData> ListItems = new ArrayList<>();
            ListItems.add(new MenuItemData("Hợp đồng bảo hiểm","",R.drawable.ic_contract_insurance));
            ListItems.add(new MenuItemData("Hợp đồng số dư sinh lời","",R.drawable.ic_accmulated_balance));
            ListGroupItem.add(new GroupItemData("",ListItems));
            ShowBottomSheet("Quản lý hợp đồng",ListGroupItem);
        });
        findViewById(R.id.my_ticket_item).setOnClickListener(v-> {
            List<GroupItemData> ListGroupItem = new ArrayList<>();
            List<MenuItemData> ListItems = new ArrayList<>();
            ListItems.add(new MenuItemData("Vé xem phim","",R.drawable.ic_movie_ticket));
            ListItems.add(new MenuItemData("Vé máy bay","",R.drawable.ic_flight_ticket));
            ListItems.add(new MenuItemData("Vé xe khách","",R.drawable.ic_bus_ticket));
            ListItems.add(new MenuItemData("Vé tàu hỏa","",R.drawable.ic_train_ticket));
            ListItems.add(new MenuItemData("Vé tham quan","",R.drawable.ic_tourist_attraction_ticket));
            ListGroupItem.add(new GroupItemData("",ListItems));
            ShowBottomSheet("Quản lý vé",ListGroupItem);
        });
        findViewById(R.id.security_center_item).setOnClickListener(v->
        {
            List<GroupItemData> ListGroupItem = new ArrayList<>();
            List<MenuItemData> ListItems = new ArrayList<>();
            ListItems.add(new MenuItemData("Bảo mật tài khoản","",R.drawable.ic_security_user));
            ListItems.add(new MenuItemData("Bảo mật giao dịch","",R.drawable.ic_security_transaction));
            ListGroupItem.add(new GroupItemData("",ListItems));
            ShowBottomSheet("Trung tâm bảo mật",ListGroupItem);
        });
        findViewById(R.id.setting_item).setOnClickListener(v->
        {
            List<GroupItemData> ListGroupItem = new ArrayList<>();
            List<MenuItemData> ListItems = new ArrayList<>();
            ListItems.add(new MenuItemData("Cài đặt thông báo","",R.drawable.ic_notification));
            ListGroupItem.add(new GroupItemData("Cài đặt cho ứng dụng UITpay",ListItems));
            List<MenuItemData> ListItems2 = new ArrayList<>();
            ListItems2.add(new MenuItemData("Thông tin ứng dụng","",R.drawable.ic_uitpay_information));
            ListItems2.add(new MenuItemData("Dọn dẹp bộ nhớ tạm","",R.drawable.ic_clean));
            ListGroupItem.add(new GroupItemData("Khác",ListItems2));
            ShowBottomSheet("Cài đặt ứng dụng",ListGroupItem);
        });
        findViewById(R.id.qr_manage_card).setOnClickListener(v->
        {
            List<GroupItemData> ListGroupItem = new ArrayList<>();
            List<MenuItemData> ListItems = new ArrayList<>();
            ListItems.add(new MenuItemData("Mã nhận tiền","Chia sẻ mã này để nhận tiền những người xung quanh",R.drawable.ic_receive));
            ListItems.add(new MenuItemData("Mã thanh toán","Đưa mã này cho thu ngân cửa hàng để thanh toán nhé",R.drawable.ic_qr_code));
            ListItems.add(new MenuItemData("Quét QR","Hướng camera vào mã QR để quyét",R.drawable.ic_scan));
            ListGroupItem.add(new GroupItemData("Các mã QR quan trọng",ListItems));
            ShowBottomSheet("Quản lý mã",ListGroupItem);
        });
        findViewById(R.id.show_more_manage_finance).setOnClickListener(v->
        {
            List<GroupItemData> ListGroupItem = new ArrayList<>();
            List<MenuItemData> ListItems = new ArrayList<>();
            ListItems.add(new MenuItemData("Tài khoản/thẻ liên kết","",R.drawable.ic_account_card_payment));
            ListItems.add(new MenuItemData("Cài đặt thanh toán tự động","Sắp xếp nguồn tiền, cài đặt dịch vụ",R.drawable.ic_payment));
            ListItems.add(new MenuItemData("Điểm tin cậy UITpay","",R.drawable.ic_reliable_score));
            ListItems.add(new MenuItemData("các liên kết ngân hàng khác","",R.drawable.ic_link_bank));
            ListGroupItem.add(new GroupItemData("",ListItems));
            ShowBottomSheet("Quản lý tài chính",ListGroupItem);
        });
    }
    public void SetLayoutMenuItem(int item_id,String item_title,String item_subtitle,int item_icon)
    {
        View item = findViewById(item_id);
        SetDetaileMenuItem(item,item_title,item_subtitle,item_icon);
    }
    private void SetDetaileMenuItem(View item,String item_title,String item_subtitle,int item_icon) {
        ((TextView)item.findViewById(R.id.menu_title)).setText(item_title);
        if (item_subtitle != "")
        {
            TextView menu_subtitle = item.findViewById(R.id.menu_subtitle);
            menu_subtitle.setVisibility(View.VISIBLE);
            menu_subtitle.setText(item_subtitle);
        }
        ((ImageView)item.findViewById(R.id.menu_icon)).setImageResource(item_icon);
    }
    public static class MenuItemData
    {
        private String title;
        private String subtitle;
        private int icon;
        public MenuItemData(String title,String subtitle,int icon)
        {
            this.title = title;
            this.subtitle = subtitle;
            this.icon = icon;
        }
    }
    public static class GroupItemData
    {
        private String title;
        private List<MenuItemData> ListItems;
        public GroupItemData(String title,List<MenuItemData> ListItems)
        {
            this.title = title;
            this.ListItems = ListItems;
        }
    }
    private void ShowBottomSheet(String SheetTilte, List<GroupItemData> ListGroupItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View SheetView=getLayoutInflater().inflate(R.layout.layout_dynamic_bottom_sheet,null);
        ((ImageView)SheetView.findViewById(R.id.btn_close)).setOnClickListener(v->bottomSheetDialog.dismiss());
        ((TextView)SheetView.findViewById(R.id.sheet_title)).setText(SheetTilte);
        LinearLayout sheet_container = SheetView.findViewById(R.id.sheet_container);
        for (GroupItemData GroupItem : ListGroupItem)
        {
            if (GroupItem.title!="")
            {
                TextView group_title = new TextView(this);
                group_title.setText(GroupItem.title);
                group_title.setTextSize(16);
                group_title.setTextColor(android.graphics.Color.parseColor("#000000"));
                group_title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                sheet_container.addView(group_title);
            }
            for (MenuItemData MenuItem : GroupItem.ListItems)
            {
                View itemView=getLayoutInflater().inflate(R.layout.menuitem_profile_screen,null);
                SetDetaileMenuItem(itemView,MenuItem.title,MenuItem.subtitle,MenuItem.icon);
                CardView cardView = new CardView(this);
                cardView.setRadius(16);
                cardView.setCardElevation(2);
                cardView.setUseCompatPadding(true);
                cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"));
                cardView.addView(itemView);
                sheet_container.addView(cardView);
            }
        }
        bottomSheetDialog.setContentView(SheetView);
        bottomSheetDialog.show();
    }
}

