package com.example.uitpayapp.merchant.marketing;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

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
    private final float[] dummyValues = {120, 150, 100, 180, 220, 200, 240, 210, 185, 260, 280, 275, 310, 295, 340, 320, 285, 350, 380, 365, 390, 420, 405, 430, 450, 465, 440, 475, 490, 505};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_statistics);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        currencyFormatter = new DecimalFormat("###,###.###", symbols);

        initViews();
        setupSpinners();
        setupLineChart();
        setupDailyList();
        updateSummary();
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

    private void updateSummary() {
        ((TextView) findViewById(R.id.tv_total_revenue)).setText(String.format("%sđ", currencyFormatter.format(9513000)));
        ((TextView) findViewById(R.id.tv_avg_revenue)).setText(String.format("%sđ", currencyFormatter.format(317100)));
        ((TextView) findViewById(R.id.tv_highest_revenue)).setText(String.format("%sđ", currencyFormatter.format(500000)));
        ((TextView) findViewById(R.id.tv_lowest_revenue)).setText(String.format("%sđ", currencyFormatter.format(98000)));
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

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setupDailyList();
                updateChart();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerMonth.setOnItemSelectedListener(listener);
        spinnerYear.setOnItemSelectedListener(listener);
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
        leftAxis.setAxisMaximum(600f);
        leftAxis.setLabelCount(5, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "0k";
                return ((int) value) + "k";
            }
        });

        lineChart.getAxisRight().setEnabled(false);
        updateChart();
    }

    private void updateChart() {
        List<Entry> entries = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        int today = calendar.get(Calendar.DAY_OF_MONTH);

        int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1;
        int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());

        int limit = dummyValues.length;
        if (selectedMonth == currentMonth && selectedYear == currentYear) {
            limit = Math.min(today, dummyValues.length);
        }

        for (int i = 0; i < limit; i++) {
            entries.add(new Entry(i + 1, dummyValues[i]));
        }

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

    private void setupDailyList() {
        List<DailyStat> stats = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        int today = calendar.get(Calendar.DAY_OF_MONTH);

        int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1;
        int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());

        int limit = dummyValues.length;
        if (selectedMonth == currentMonth && selectedYear == currentYear) {
            limit = Math.min(today, dummyValues.length);
        }

        for (int i = limit - 1; i >= 0; i--) {
            String dateLabel = String.format(Locale.getDefault(), "Ngày %02d/%02d/%d", (i + 1), selectedMonth, selectedYear);
            stats.add(new DailyStat(dateLabel, dummyValues[i] * 1000));
        }

        rvDailyStats.setLayoutManager(new LinearLayoutManager(this));
        rvDailyStats.setAdapter(new DailyStatAdapter(stats, currencyFormatter));
        rvDailyStats.setNestedScrollingEnabled(false);
    }
}
