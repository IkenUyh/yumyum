package com.example.uitpayapp.merchant.marketing;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.statistic.StatisticRepository;
import com.example.uitpayapp.modules.statistic.models.responses.DailyRevenueStatItem;
import com.example.uitpayapp.modules.statistic.models.responses.MerchantMonthlyStatisticResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SellerStatisticsActivity extends AppCompatActivity {

    private Spinner spinnerMonth, spinnerYear;
    private LineChart lineChart;
    private RecyclerView rvDailyStats;
    private DecimalFormat currencyFormatter;

    private StatisticRepository statisticRepository;
    private Long restaurantId;

    // Track if spinners have been initialized to avoid double-loading on setup
    private boolean spinnersReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_statistics);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        currencyFormatter = new DecimalFormat("###,###.###", symbols);

        statisticRepository = new StatisticRepository();

        // Lấy restaurantId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
        restaurantId = prefs.getLong("current_store_id", -1L);

        initViews();
        setupLineChart();
        setupSpinners(); // triggers loadMonthlyStats after setting spinnersReady = true
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        spinnerMonth = findViewById(R.id.spinner_month);
        spinnerYear = findViewById(R.id.spinner_year);
        lineChart = findViewById(R.id.line_chart);
        rvDailyStats = findViewById(R.id.rv_daily_stats);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.seller_statistics_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSpinners() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = "Tháng " + (i + 1);
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        Calendar calendar = Calendar.getInstance();
        spinnerMonth.setSelection(calendar.get(Calendar.MONTH));

        // Dynamic Year Spinner from 2022 to Current Year
        int startYear = 2022;
        int currentYear = calendar.get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int y = startYear; y <= currentYear; y++) {
            years.add(String.valueOf(y));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(years.size() - 1);

        spinnersReady = true;

        // Load initial data after both spinners are set
        loadMonthlyStats();

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnersReady) {
                    loadMonthlyStats();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerMonth.setOnItemSelectedListener(listener);
        spinnerYear.setOnItemSelectedListener(listener);
    }

    private void loadMonthlyStats() {
        if (restaurantId == null || restaurantId == -1L) {
            Toast.makeText(this, "Không tìm thấy thông tin cửa hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1;
        int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());

        statisticRepository.getMerchantMonthlyStatistic(restaurantId, selectedMonth, selectedYear,
                new ApiCallback<MerchantMonthlyStatisticResponse>() {
                    @Override
                    public void onSuccess(MerchantMonthlyStatisticResponse data) {
                        runOnUiThread(() -> {
                            updateSummary(data);
                            updateChart(data.getDailyStats());
                            setupDailyList(data.getDailyStats(), selectedMonth, selectedYear);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() ->
                                Toast.makeText(SellerStatisticsActivity.this,
                                        "Lỗi tải dữ liệu: " + message, Toast.LENGTH_SHORT).show()
                        );
                    }
                });
    }

    private void updateSummary(MerchantMonthlyStatisticResponse data) {
        BigDecimal totalRevenue = data.getTotalRevenue() != null ? data.getTotalRevenue() : BigDecimal.ZERO;
        BigDecimal avgRevenue = data.getAvgDailyRevenue() != null ? data.getAvgDailyRevenue() : BigDecimal.ZERO;
        BigDecimal highestRevenue = data.getHighestDailyRevenue() != null ? data.getHighestDailyRevenue() : BigDecimal.ZERO;
        BigDecimal lowestRevenue = data.getLowestDailyRevenue() != null ? data.getLowestDailyRevenue() : BigDecimal.ZERO;

        ((TextView) findViewById(R.id.tv_total_revenue)).setText(
                String.format("%sđ", currencyFormatter.format(totalRevenue)));
        ((TextView) findViewById(R.id.tv_avg_revenue)).setText(
                String.format("%sđ", currencyFormatter.format(avgRevenue)));
        ((TextView) findViewById(R.id.tv_highest_revenue)).setText(
                String.format("%sđ", currencyFormatter.format(highestRevenue)));
        ((TextView) findViewById(R.id.tv_lowest_revenue)).setText(
                String.format("%sđ", currencyFormatter.format(lowestRevenue)));
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setExtraOffsets(10f, 10f, 10f, 10f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.parseColor("#F0F0F0"));
        xAxis.setAxisLineColor(Color.parseColor("#CCCCCC"));
        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setLabelCount(10, true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%02d", (int) value);
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#F0F0F0"));
        leftAxis.setAxisLineColor(Color.parseColor("#CCCCCC"));
        leftAxis.setTextColor(Color.parseColor("#999999"));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(5, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "0";
                if (value >= 1_000_000) return ((int)(value / 1_000_000)) + "M";
                if (value >= 1_000) return ((int)(value / 1_000)) + "k";
                return String.valueOf((int) value);
            }
        });

        lineChart.getAxisRight().setEnabled(false);
    }

    private void updateChart(List<DailyRevenueStatItem> dailyStats) {
        List<Entry> entries = new ArrayList<>();
        float maxRevenue = 0f;

        if (dailyStats != null) {
            for (DailyRevenueStatItem stat : dailyStats) {
                float revenue = stat.getRevenue() != null ? stat.getRevenue().floatValue() : 0f;
                entries.add(new Entry(stat.getDay(), revenue));
                if (revenue > maxRevenue) maxRevenue = revenue;
            }
        }

        // Set Y-axis max with a 20% headroom
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMaximum(maxRevenue > 0 ? maxRevenue * 1.2f : 100_000f);

        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        int brandColor = Color.parseColor("#F24405");
        dataSet.setColor(brandColor);
        dataSet.setCircleColor(brandColor);
        dataSet.setLineWidth(1.5f);
        dataSet.setCircleRadius(3.5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.animateX(800);
        lineChart.invalidate();
    }

    private void setupDailyList(List<DailyRevenueStatItem> dailyStats, int month, int year) {
        List<DailyStat> stats = new ArrayList<>();

        if (dailyStats != null) {
            // Show in reverse order (newest first)
            for (int i = dailyStats.size() - 1; i >= 0; i--) {
                DailyRevenueStatItem item = dailyStats.get(i);
                float revenue = item.getRevenue() != null ? item.getRevenue().floatValue() : 0f;
                String dateLabel = String.format(Locale.getDefault(),
                        "Ngày %02d/%02d/%d", item.getDay(), month, year);
                stats.add(new DailyStat(dateLabel, revenue));
            }
        }

        rvDailyStats.setLayoutManager(new LinearLayoutManager(this));
        rvDailyStats.setAdapter(new DailyStatAdapter(stats, currencyFormatter));
        rvDailyStats.setNestedScrollingEnabled(false);
    }
}
