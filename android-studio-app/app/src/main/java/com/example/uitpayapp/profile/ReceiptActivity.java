package com.example.uitpayapp.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.ServiceAdapter;
import com.example.uitpayapp.home.ServiceItem;
import com.example.uitpayapp.suggestion.SuggestionModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ReceiptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        View topBar=findViewById(R.id.top_bar_receipt);
        View mainContainer=findViewById(R.id.receipt_screen_main_data);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom+10;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        ((TextView)topBar.findViewById(R.id.top_bar_title)).setText("Hóa đơn");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v->finish());
        TextView receiptBanner=findViewById(R.id.receipt_banner);
        receiptBanner.setSelected(true);
        List<ServiceItem> listReceiptService = new ArrayList<>();
        listReceiptService.add(new ServiceItem("Điện", R.drawable.ic_receiptscreen_electric, ""));
        listReceiptService.add(new ServiceItem("Nước", R.drawable.ic_receiptscreen_water, ""));
        listReceiptService.add(new ServiceItem("Thanh toán\nkhoản vay", R.drawable.ic_receiptscreen_loan, "-20K"));
        listReceiptService.add(new ServiceItem("Bảo hiểm",R.drawable.ic_receiptscreen_heath,""));
        listReceiptService.add(new ServiceItem("Giáo dục",R.drawable.ic_receiptscreen_education,""));
        listReceiptService.add(new ServiceItem("Truyền hình",R.drawable.ic_receiptscreen_tv,""));
        listReceiptService.add(new ServiceItem("Internet", R.drawable.ic_internet, ""));
        listReceiptService.add(new ServiceItem("Tài khoản\ntrả sau",R.drawable.ic_autopay_paylater,"+15Tr"));
        RecyclerView rvReceiptService = findViewById(R.id.rv_bill_services);
        rvReceiptService.setAdapter(new ServiceAdapter(listReceiptService, R.layout.item_service, item -> {}));
        SetDataPieChart();
        SetSpecialOffer();
    }
    private void SetDataPieChart()
    {
        PieChart pieChart = findViewById(R.id.receipt_pie_chart);
        pieChart.getDescription().setEnabled(false);
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(500000f, "Điện"));
        pieEntries.add(new PieEntry(30000f, "Nước"));
        pieEntries.add(new PieEntry(200000f, "Internet"));

        //Dua list vo dataset cua so do
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(10f);
        //Dua data vo chart
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
    private void SetSpecialOffer() {
        List<SuggestionModel> listSpecialOffer = new ArrayList<>();
        listSpecialOffer.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL, R.drawable.img_miku, "Tặng xe điện và chia 20 triệu xu","Khi thanh toán hóa đơn"));
        listSpecialOffer.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL, R.drawable.img_miku, "Mới! Miễn phí hóa đơn Nước","Thanh toán ngay!"));
        listSpecialOffer.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL, R.drawable.img_miku, "Giảm đến 50% khi thanh toán Hi FPT",""));
        listSpecialOffer.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL, R.drawable.img_miku, "Ưu đãi cho tất cả khách hàng","Giảm 50K & 20K"));
        listSpecialOffer.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL, R.drawable.img_miku, "Ưu đãi khoản thu đến 100.000đ","Giáo dục"));
        listSpecialOffer.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL, R.drawable.img_miku, "Đi du lịch đãaaa, trả sau!","Giảm đến 50%"));
        RecyclerView rvSpecialOffer = findViewById(R.id.rv_receipt_special_offers);
        rvSpecialOffer.setAdapter(new com.example.uitpayapp.suggestion.SuggestAdapter(listSpecialOffer));
        TextView receiptBanner=findViewById(R.id.receipt_banner);
        receiptBanner.setSelected(true);
    }
}
