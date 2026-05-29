package com.example.uitpayapp.home.receipt;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.ServiceAdapter;
import com.example.uitpayapp.home.ServiceItem;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ReceiptActivity extends AppCompatActivity {

    private List<PendingBill> pendingBills;
    private PendingBillAdapter billAdapter;
    private String currentPin = "";
    private View[] pinDots = new View[6];

    private int pendingBillPosition = -1;
    private boolean pendingIsAutoPay = false;

    // Map tên dịch vụ → thông tin nhà cung cấp
    private static final Map<String, String[]> SERVICE_PROVIDERS = new HashMap<>();
    static {
        SERVICE_PROVIDERS.put("Điện", new String[]{"EVN Hồ Chí Minh", "300000", "800000"});
        SERVICE_PROVIDERS.put("Nước", new String[]{"SAWACO", "20000", "80000"});
        SERVICE_PROVIDERS.put("Internet", new String[]{"FPT Telecom", "150000", "350000"});
        SERVICE_PROVIDERS.put("Truyền hình", new String[]{"VTVcab", "80000", "200000"});
        SERVICE_PROVIDERS.put("Bảo hiểm", new String[]{"Bảo Việt", "500000", "1500000"});
        SERVICE_PROVIDERS.put("Giáo dục", new String[]{"ĐH CNTT - ĐHQG", "5000000", "12000000"});
        SERVICE_PROVIDERS.put("Tài khoản\ntrả sau", new String[]{"Mobifone", "100000", "500000"});
        SERVICE_PROVIDERS.put("Thanh toán\nkhoản vay", new String[]{"VPBank", "2000000", "5000000"});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        View topBar = findViewById(R.id.layout_header);
        View mainContainer = findViewById(R.id.receipt_screen_main_data);

        if (mainContainer != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
                Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                int safeBottomPadding = navInsets.bottom + 10;
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), safeBottomPadding);
                return insets;
            });
        }

        topBar.findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        TextView receiptBanner = findViewById(R.id.receipt_banner);
        receiptBanner.setSelected(true);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        TextView tvChartMonth = findViewById(R.id.tv_chart_month);
        tvChartMonth.setText(sdf.format(new Date()) + " ▾");

        setupBillServices();
        setupPendingBills();
        setupPieChart();
        updateSummary();

        findViewById(R.id.btn_pay_all).setOnClickListener(v -> payAllBills());
    }

    // ============ Grid dịch vụ → Click mở form đăng ký hóa đơn ============

    private void setupBillServices() {
        List<ServiceItem> listReceiptService = new ArrayList<>();
        listReceiptService.add(new ServiceItem("Điện", R.drawable.ic_receiptscreen_electric, ""));
        listReceiptService.add(new ServiceItem("Nước", R.drawable.ic_receiptscreen_water, ""));
        listReceiptService.add(new ServiceItem("Thanh toán\nkhoản vay", R.drawable.ic_your_deal, ""));
        listReceiptService.add(new ServiceItem("Bảo hiểm", R.drawable.ic_receiptscreen_heath, ""));
        listReceiptService.add(new ServiceItem("Giáo dục", R.drawable.ic_receiptscreen_education, ""));
        listReceiptService.add(new ServiceItem("Truyền hình", R.drawable.ic_receiptscreen_tv, ""));
        listReceiptService.add(new ServiceItem("Internet", R.drawable.ic_internet, ""));
        listReceiptService.add(new ServiceItem("Tài khoản\ntrả sau", R.drawable.ic_autopay_paylater, ""));

        RecyclerView rvReceiptService = findViewById(R.id.rv_bill_services);
        rvReceiptService.setAdapter(new ServiceAdapter(listReceiptService, R.layout.item_service,
            item -> showAddBillBottomSheet(item)
        ));
    }

    private void showAddBillBottomSheet(ServiceItem service) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_add_account_payment, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btn_close_add_bill).setOnClickListener(v -> dialog.dismiss());

        // Thiết lập dữ liệu cho dropdown đơn vị cung cấp (với Logo và Subtitle)
        AutoCompleteTextView dropdown = view.findViewById(R.id.dropdown);
        List<ProviderItem> providerList = new ArrayList<>();
        String serviceName = service.getName();

        if (serviceName.contains("Điện")) {
            providerList.add(new ProviderItem("EVN Hồ Chí Minh", "Tổng công ty Điện lực TP.HCM", R.drawable.ic_evn));
            providerList.add(new ProviderItem("EVN Hà Nội", "Tổng công ty Điện lực TP.Hà Nội", R.drawable.ic_evn));
            providerList.add(new ProviderItem("EVN Miền Nam", "Tổng công ty Điện lực Miền Nam", R.drawable.ic_evn));
            providerList.add(new ProviderItem("EVN Miền Trung", "Tổng công ty Điện lực Miền Trung", R.drawable.ic_evn));
        } else if (serviceName.contains("Nước")) {
            providerList.add(new ProviderItem("SAWACO", "Tổng công ty Cấp nước Sài Gòn", R.drawable.ic_receiptscreen_water));
            providerList.add(new ProviderItem("Viwasupco", "Công ty CP Đầu tư nước sạch Sông Đà", R.drawable.ic_receiptscreen_water));
            providerList.add(new ProviderItem("Biwase", "Công ty CP - MTV Nước - Môi trường Bình Dương", R.drawable.ic_receiptscreen_water));
            providerList.add(new ProviderItem("Nước sạch Hà Nội", "Công ty TNHH MTV Nước sạch Hà Nội", R.drawable.ic_receiptscreen_water));
        } else if (serviceName.contains("Internet")) {
            providerList.add(new ProviderItem("FPT Telecom", "Công ty CP Viễn thông FPT", R.drawable.ic_internet));
            providerList.add(new ProviderItem("Viettel Telecom", "Tổng Công ty Viễn thông Viettel", R.drawable.img_viettel));
            providerList.add(new ProviderItem("VNPT", "Tập đoàn Bưu chính Viễn thông Việt Nam", R.drawable.ic_internet));
        } else if (serviceName.contains("Truyền hình")) {
            providerList.add(new ProviderItem("VTVcab", "Tổng Công ty Truyền hình Cáp Việt Nam", R.drawable.ic_receiptscreen_tv));
            providerList.add(new ProviderItem("SCTV", "Công ty Truyền hình cáp Saigontourist", R.drawable.ic_receiptscreen_tv));
            providerList.add(new ProviderItem("FPT Play", "Dịch vụ truyền hình FPT", R.drawable.ic_receiptscreen_tv));
        } else if (serviceName.contains("Bảo hiểm")) {
            providerList.add(new ProviderItem("Bảo Việt", "Tập đoàn Bảo hiểm Bảo Việt", R.drawable.ic_receiptscreen_heath));
            providerList.add(new ProviderItem("Prudential", "Bảo hiểm nhân thọ Prudential", R.drawable.ic_receiptscreen_heath));
            providerList.add(new ProviderItem("Manulife", "Bảo hiểm nhân thọ Manulife", R.drawable.ic_receiptscreen_heath));
        } else if (serviceName.contains("Giáo dục")) {
            providerList.add(new ProviderItem("ĐH CNTT - ĐHQG", "Trường Đại học Công nghệ Thông tin", R.drawable.logo_uit_updated));
            providerList.add(new ProviderItem("ĐH Bách Khoa", "Trường Đại học Bách Khoa TP.HCM", R.drawable.ic_receiptscreen_education));
            providerList.add(new ProviderItem("Vinschool", "Hệ thống giáo dục Vinschool", R.drawable.ic_receiptscreen_education));
        } else if (serviceName.contains("trả sau")) {
            providerList.add(new ProviderItem("Mobifone", "Tổng công ty Viễn thông MobiFone", R.drawable.img_mobifone));
            providerList.add(new ProviderItem("Viettel", "Tổng Công ty Viễn thông Viettel", R.drawable.img_viettel));
            providerList.add(new ProviderItem("Vinaphone", "Tổng công ty Dịch vụ Viễn thông VNPT", R.drawable.img_vinaphone));
        } else if (serviceName.contains("khoản vay")) {
            providerList.add(new ProviderItem("Home Credit", "Công ty tài chính Home Credit", R.drawable.ic_your_deal));
            providerList.add(new ProviderItem("FE Credit", "Công ty tài chính FE Credit", R.drawable.ic_your_deal));
            providerList.add(new ProviderItem("Shinhan Finance", "Công ty tài chính Shinhan Việt Nam", R.drawable.ic_your_deal));
        };

        ProviderAdapter providerAdapter = new ProviderAdapter(this, providerList);
        dropdown.setAdapter(providerAdapter);
        if (!providerList.isEmpty()) {
            dropdown.setText(providerList.get(0).getTitle(), false);
        }
        dropdown.setOnItemClickListener((parent, view1, position, id) -> {
                String selectedProvider = providerList.get(position).getTitle();
                dropdown.setText(selectedProvider, false);
        });

        EditText etCode = view.findViewById(R.id.et_customer_code);

        view.findViewById(R.id.btn_confirm_add_bill).setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã khách hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedProvider = dropdown.getText().toString();

            // Kiểm tra trùng lặp
            String serviceTitle = service.getName().replace("\n", " ");
            for (PendingBill bill : pendingBills) {
                if (bill.getName().equals(serviceTitle)) {
                    Toast.makeText(this, "Hóa đơn " + serviceTitle + " đã tồn tại", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
            }

            // Tạo hóa đơn giả lập
            PendingBill newBill = generateMockBill(service, selectedProvider);
            pendingBills.add(0, newBill); // Thêm lên đầu danh sách
            billAdapter.notifyItemInserted(0);

            RecyclerView rv = findViewById(R.id.rv_pending_bills);
            rv.scrollToPosition(0);

            updateSummary();
            dialog.dismiss();
            Toast.makeText(this, "Đã thêm hóa đơn " + serviceTitle + " từ " + selectedProvider, Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private PendingBill generateMockBill(ServiceItem service, String provider) {
        String serviceName = service.getName().replace("\n", " ");
        Random random = new Random();

        // Lấy thông tin khoảng tiền
        long minAmount = 100000, maxAmount = 500000;
        String[] info = SERVICE_PROVIDERS.get(service.getName());
        if (info != null) {
            minAmount = Long.parseLong(info[1]);
            maxAmount = Long.parseLong(info[2]);
        }

        // Random số tiền trong khoảng, làm tròn đến 1000đ
        long amount = minAmount + (long)(random.nextDouble() * (maxAmount - minAmount));
        amount = (amount / 1000) * 1000;

        // Hạn thanh toán: 10-25 ngày tới
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 10 + random.nextInt(16));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dueDate = sdf.format(cal.getTime());

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String amountStr = formatter.format(amount) + "đ";

        return new PendingBill(serviceName, provider, dueDate, amountStr, service.getIconResId(), false);
    }

    // ============ Danh sách hóa đơn chờ thanh toán ============

    private void setupPendingBills() {
        pendingBills = new ArrayList<>();
        // Một vài hóa đơn mẫu đã đăng ký sẵn
        pendingBills.add(new PendingBill("Tiền điện", "EVN Hồ Chí Minh", "15/05/2026", "500.000đ", R.drawable.ic_receiptscreen_electric, false));
        pendingBills.add(new PendingBill("Internet", "FPT Telecom", "25/05/2026", "200.000đ", R.drawable.ic_internet, false));

        billAdapter = new PendingBillAdapter(pendingBills,
            // Click → chọn nguồn tiền → nhập PIN → thanh toán
            (bill, position) -> {
                if (!bill.isPaid()) {
                    pendingBillPosition = position;
                    pendingIsAutoPay = false;
                    showDestinationSelection();
                } else {
                    Toast.makeText(this, bill.getName() + " đã được thanh toán", Toast.LENGTH_SHORT).show();
                }
            },
            // Bật auto-pay → chọn nguồn tiền → nhập PIN
            (bill, position, isEnabled) -> {
                if (isEnabled) {
                    pendingBillPosition = position;
                    pendingIsAutoPay = true;
                    showDestinationSelection();
                } else {
                    Toast.makeText(this, "Đã tắt tự động thanh toán cho " + bill.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        );

        RecyclerView rvPendingBills = findViewById(R.id.rv_pending_bills);
        rvPendingBills.setLayoutManager(new LinearLayoutManager(this));
        rvPendingBills.setAdapter(billAdapter);
    }

    // ============ LUỒNG: Chọn nguồn tiền → Nhập PIN → Hoàn thành ============

    private void showDestinationSelection() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_destination, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_destination_title)).setText("Chọn nguồn tiền");

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btn_close_destination).setOnClickListener(v -> {
            dialog.dismiss();
            if (pendingIsAutoPay && pendingBillPosition >= 0) {
                pendingBills.get(pendingBillPosition).setAutoPay(false);
                billAdapter.notifyItemChanged(pendingBillPosition);
            }
        });

        dialog.setOnCancelListener(d -> {
            if (pendingIsAutoPay && pendingBillPosition >= 0) {
                pendingBills.get(pendingBillPosition).setAutoPay(false);
                billAdapter.notifyItemChanged(pendingBillPosition);
            }
        });

        view.findViewById(R.id.btn_dest_wallet).setOnClickListener(v -> {
            dialog.dismiss();
            showPasscodeBottomSheet("Ví UIT Pay");
        });

        view.findViewById(R.id.btn_dest_saving).setOnClickListener(v -> {
            dialog.dismiss();
            showPasscodeBottomSheet("Quỹ tiết kiệm");
        });

        dialog.show();
    }

    private void showPasscodeBottomSheet(String source) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_passcode, null);
        dialog.setContentView(sheetView);

        pinDots[0] = sheetView.findViewById(R.id.dot_1);
        pinDots[1] = sheetView.findViewById(R.id.dot_2);
        pinDots[2] = sheetView.findViewById(R.id.dot_3);
        pinDots[3] = sheetView.findViewById(R.id.dot_4);
        pinDots[4] = sheetView.findViewById(R.id.dot_5);
        pinDots[5] = sheetView.findViewById(R.id.dot_6);

        sheetView.findViewById(R.id.btn_close_passcode).setOnClickListener(v -> {
            dialog.dismiss();
            if (pendingIsAutoPay && pendingBillPosition >= 0) {
                pendingBills.get(pendingBillPosition).setAutoPay(false);
                billAdapter.notifyItemChanged(pendingBillPosition);
            }
        });

        dialog.setOnCancelListener(d -> {
            if (pendingIsAutoPay && pendingBillPosition >= 0) {
                pendingBills.get(pendingBillPosition).setAutoPay(false);
                billAdapter.notifyItemChanged(pendingBillPosition);
            }
        });

        int[] numberButtonIds = {
            R.id.btn_pin_0, R.id.btn_pin_1, R.id.btn_pin_2, R.id.btn_pin_3, R.id.btn_pin_4,
            R.id.btn_pin_5, R.id.btn_pin_6, R.id.btn_pin_7, R.id.btn_pin_8, R.id.btn_pin_9
        };

        for (int id : numberButtonIds) {
            sheetView.findViewById(id).setOnClickListener(v -> {
                if (currentPin.length() < 6) {
                    currentPin += v.getTag().toString();
                    updatePinDots();

                    if (currentPin.length() == 6) {
                        new Handler().postDelayed(() -> {
                            dialog.dismiss();
                            onPaymentComplete(source);
                        }, 300);
                    }
                }
            });
        }

        sheetView.findViewById(R.id.btn_pin_delete).setOnClickListener(v -> {
            if (currentPin.length() > 0) {
                currentPin = currentPin.substring(0, currentPin.length() - 1);
                updatePinDots();
            }
        });

        currentPin = "";
        updatePinDots();
        dialog.show();
    }

    private void updatePinDots() {
        int colorBlue = Color.parseColor("#0A46A6");
        int colorGray = Color.parseColor("#E0E0E0");
        for (int i = 0; i < 6; i++) {
            if (i < currentPin.length()) {
                pinDots[i].setBackgroundTintList(ColorStateList.valueOf(colorBlue));
            } else {
                pinDots[i].setBackgroundTintList(ColorStateList.valueOf(colorGray));
            }
        }
    }

    private void onPaymentComplete(String source) {
        if (pendingBillPosition < 0 || pendingBillPosition >= pendingBills.size()) return;
        PendingBill bill = pendingBills.get(pendingBillPosition);

        if (pendingIsAutoPay) {
            bill.setAutoPay(true);
            billAdapter.notifyItemChanged(pendingBillPosition);
            Toast.makeText(this,
                "Đã bật tự động thanh toán " + bill.getName() + " từ " + source,
                Toast.LENGTH_SHORT).show();
        } else {
            bill.setPaid(true);
            billAdapter.notifyItemChanged(pendingBillPosition);
            updateSummary();
            Toast.makeText(this,
                "Đã thanh toán " + bill.getName() + " từ " + source,
                Toast.LENGTH_SHORT).show();
        }

        pendingBillPosition = -1;
    }

    // ============ Thanh toán tất cả ============

    private void payAllBills() {
        boolean hasUnpaid = false;
        for (PendingBill bill : pendingBills) {
            if (!bill.isPaid()) {
                hasUnpaid = true;
                break;
            }
        }
        if (!hasUnpaid) {
            Toast.makeText(this, "Tất cả hóa đơn đã được thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        pendingBillPosition = -999;
        pendingIsAutoPay = false;
        showDestinationSelectionForAll();
    }

    private void showDestinationSelectionForAll() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_destination, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_destination_title)).setText("Chọn nguồn tiền");

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btn_close_destination).setOnClickListener(v -> dialog.dismiss());

        view.findViewById(R.id.btn_dest_wallet).setOnClickListener(v -> {
            dialog.dismiss();
            showPasscodeForAll("Ví UIT Pay");
        });

        view.findViewById(R.id.btn_dest_saving).setOnClickListener(v -> {
            dialog.dismiss();
            showPasscodeForAll("Quỹ tiết kiệm");
        });

        dialog.show();
    }

    private void showPasscodeForAll(String source) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_passcode, null);
        dialog.setContentView(sheetView);

        pinDots[0] = sheetView.findViewById(R.id.dot_1);
        pinDots[1] = sheetView.findViewById(R.id.dot_2);
        pinDots[2] = sheetView.findViewById(R.id.dot_3);
        pinDots[3] = sheetView.findViewById(R.id.dot_4);
        pinDots[4] = sheetView.findViewById(R.id.dot_5);
        pinDots[5] = sheetView.findViewById(R.id.dot_6);

        sheetView.findViewById(R.id.btn_close_passcode).setOnClickListener(v -> dialog.dismiss());

        int[] numberButtonIds = {
            R.id.btn_pin_0, R.id.btn_pin_1, R.id.btn_pin_2, R.id.btn_pin_3, R.id.btn_pin_4,
            R.id.btn_pin_5, R.id.btn_pin_6, R.id.btn_pin_7, R.id.btn_pin_8, R.id.btn_pin_9
        };

        for (int id : numberButtonIds) {
            sheetView.findViewById(id).setOnClickListener(v -> {
                if (currentPin.length() < 6) {
                    currentPin += v.getTag().toString();
                    updatePinDots();

                    if (currentPin.length() == 6) {
                        new Handler().postDelayed(() -> {
                            dialog.dismiss();
                            executePayAll(source);
                        }, 300);
                    }
                }
            });
        }

        sheetView.findViewById(R.id.btn_pin_delete).setOnClickListener(v -> {
            if (currentPin.length() > 0) {
                currentPin = currentPin.substring(0, currentPin.length() - 1);
                updatePinDots();
            }
        });

        currentPin = "";
        updatePinDots();
        dialog.show();
    }

    private void executePayAll(String source) {
        Handler handler = new Handler();
        int delay = 0;
        for (int i = 0; i < pendingBills.size(); i++) {
            PendingBill bill = pendingBills.get(i);
            if (!bill.isPaid()) {
                int finalI = i;
                delay += 400;
                handler.postDelayed(() -> {
                    bill.setPaid(true);
                    billAdapter.notifyItemChanged(finalI);
                    updateSummary();
                }, delay);
            }
        }
        handler.postDelayed(() ->
            Toast.makeText(this, "Đã thanh toán tất cả từ " + source, Toast.LENGTH_SHORT).show(),
            delay + 200
        );
    }

    // ============ Biểu đồ & Tổng kết ============

    private void setupPieChart() {
        PieChart pieChart = findViewById(R.id.receipt_pie_chart);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setCenterText("Chi tiêu");
        pieChart.setCenterTextSize(14f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(false);
        // Hiện biểu đồ trống ban đầu
        updatePieChart();
    }

    private void updatePieChart() {
        PieChart pieChart = findViewById(R.id.receipt_pie_chart);
        List<PieEntry> pieEntries = new ArrayList<>();

        for (PendingBill bill : pendingBills) {
            if (bill.isPaid()) {
                String amountStr = bill.getAmount().replaceAll("[^0-9]", "");
                try {
                    float amount = Float.parseFloat(amountStr);
                    pieEntries.add(new PieEntry(amount, bill.getName()));
                } catch (NumberFormatException ignored) {}
            }
        }

        if (pieEntries.isEmpty()) {
            pieChart.clear();
            pieChart.setHoleRadius(70f);
            pieChart.setCenterText("Tháng này chưa\nthanh toán hóa đơn nào");
            pieChart.setCenterTextSize(13f);
            pieChart.setCenterTextColor(Color.parseColor("#9E9E9E"));
            pieChart.invalidate();
            return;
        }
        pieChart.setHoleRadius(40f);
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(Color.BLACK);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(11f);
        pieDataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Chi tiêu");
        pieChart.animateY(500);
        pieChart.invalidate();
    }

    private void updateSummary() {
        int paidCount = 0;
        long totalSpent = 0;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (PendingBill bill : pendingBills) {
            if (bill.isPaid()) {
                paidCount++;
                String amountStr = bill.getAmount().replaceAll("[^0-9]", "");
                try {
                    totalSpent += Long.parseLong(amountStr);
                } catch (NumberFormatException ignored) {}
            }
        }

        TextView tvTotalSpent = findViewById(R.id.tv_total_spent);
        TextView tvTotalBillsPaid = findViewById(R.id.tv_total_bills_paid);

        tvTotalSpent.setText(formatter.format(totalSpent) + "đ");
        tvTotalBillsPaid.setText(String.valueOf(paidCount));

        // Cập nhật biểu đồ theo dữ liệu thực
        updatePieChart();
    }
}
