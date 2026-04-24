package com.example.uitpayapp.voucher;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_voucher);
        View topBar = findViewById(R.id.top_bar_voucher);
        View mainContainer = findViewById(R.id.voucher_screen_container);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar=insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top,systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        ((TextView)topBar.findViewById(R.id.top_bar_title)).setText("Cài đặt thông báo");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v->finish());
        //Thiet lap thong tin cho nut tim them voucher
        SetTextButtonFindNewVoucher();
        SetDataVoucher();
    }
    private void SetTextButtonFindNewVoucher() {
        View buttonFindNewVoucher = findViewById(R.id.button_find_new_voucher);
        ((ImageView)buttonFindNewVoucher.findViewById(R.id.menu_icon)).setImageResource(R.drawable.ic_my_gift);
        ((TextView)buttonFindNewVoucher.findViewById(R.id.menu_title)).setText("Tìm thêm ưu đãi");
    }
    private void SetDataVoucher() {
        List<VoucherModel> listVoucher = new ArrayList<>();
        listVoucher.add(new VoucherModel(R.drawable.ic_uitpay_demo,"Trả khoản vay","Giảm 1% tối đa 20.000đ","Với thanh toán từ 100.000VND","HSD: 06/05/2026"));
        listVoucher.add(new VoucherModel(R.drawable.ic_uitpay_demo,"Điện","Giảm 1% tối đa 50.000đ","","HSD: 06/05/2026"));
        listVoucher.add(new VoucherModel(R.drawable.ic_uitpay_demo,"Nạp thẻ","Giảm 1% tối đa 20.000đ","","HSD: 06/05/2026"));
        listVoucher.add(new VoucherModel(R.drawable.ic_uitpay_demo,"Nước","Giảm 10% tối đa 100.000đ","","HSD: 06/05/2026"));
        RecyclerView rvVoucher = findViewById(R.id.rv_voucher);
        VoucherAdapter adapter = new VoucherAdapter(listVoucher, voucher-> HanleVoucherClick(voucher));
        rvVoucher.setAdapter(adapter);
    }
    private void HanleVoucherClick(VoucherModel voucher) {
    }
}
