package com.example.uitpayapp.history;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.util.ArrayList;
import java.util.List;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.CartItem;

public class OrderDetailActivity extends AppCompatActivity {

    // 1. Hệ thống điều khiển & Giao diện trượt
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private View mapContainer;
    private View layoutDriverInfo;
    private Toolbar toolbar;

    // 2. Hệ thống nút bấm tương tác
    private ImageButton btnMapBack;
    private ImageButton btnCallDriver;
    private ImageButton btnChatDriver;
    private Button btnCancelOrder;

    // 3. Danh sách hiển thị món ăn
    private RecyclerView rvOrderItems;

    // 4. Các trường hiển thị văn bản (Trạng thái & Địa chỉ)
    private TextView tvOrderTimeHeader;
    private TextView tvOrderStatusTitle;
    private TextView tvMerchantName;
    private TextView tvDestAddress;

    // 5. Các trường hiển thị dòng tiền (Tính toán hóa đơn)
    private TextView tvTotalItemsTitle;
    private TextView tvItemsSubtotal;
    private TextView tvShipFee;
    private TextView tvDiscount;
    private TextView tvTotalPaid;

    // 6. Khối thông tin chi tiết đơn hàng (Card 4)
    private TextView tvDetailOrderId;
    private TextView tvOrderPlacedTime;
    private TextView tvOrderNote;
    private TextView tvPaymentMethod;
    private TextView btnCopyOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        
        getWindow().setNavigationBarColor(android.graphics.Color.WHITE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        // Ánh xạ toàn bộ hệ thống điều khiển
        mapContainer = findViewById(R.id.mapContainer);
        toolbar = findViewById(R.id.toolbar);
        btnMapBack = findViewById(R.id.btnMapBack);
        btnCallDriver = findViewById(R.id.btnCallDriver);
        btnChatDriver = findViewById(R.id.btnChatDriver);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        View bottomSheet = findViewById(R.id.bottomSheetOrderDetail);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Ánh xạ các thành phần danh sách & thông tin chính
        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvOrderTimeHeader = findViewById(R.id.tvOrderTimeHeader);
        tvOrderStatusTitle = findViewById(R.id.tvOrderStatusTitle);
        layoutDriverInfo = findViewById(R.id.layoutDriverInfo);
        tvMerchantName = findViewById(R.id.tvMerchantName);
        tvDestAddress = findViewById(R.id.tvDestAddress);

        // Ánh xạ các trường tính toán hóa đơn
        tvTotalItemsTitle = findViewById(R.id.tvTotalItemsTitle);
        tvItemsSubtotal = findViewById(R.id.tvItemsSubtotal);
        tvShipFee = findViewById(R.id.tvShipFee);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotalPaid = findViewById(R.id.tvTotalPaid);

        // Ánh xạ các trường thông tin mã đơn (Card 4)
        tvDetailOrderId = findViewById(R.id.tvDetailOrderId);
        btnCopyOrderId = findViewById(R.id.btnCopyOrderId);
        tvOrderPlacedTime = findViewById(R.id.tvOrderPlacedTime);
        tvOrderNote = findViewById(R.id.tvOrderNote);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);

        // Thiết lập RecyclerView LayoutManager
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null) {
            String orderId = intent.getStringExtra("ORDER_ID");
            String orderStatus = intent.getStringExtra("ORDER_STATUS");
            long deliveryFee = intent.getLongExtra("KEY_DELIVERY_FEE", 15000);
            long discount = intent.getLongExtra("KEY_DISCOUNT", 0);

            OrderDetail mockData = new OrderDetail();
            mockData.setOrderId(orderId != null ? orderId : "05066-620675729");
            mockData.setCustomerPhone("+84987301126");
            mockData.setMerchantName("UIT FOOD");
            mockData.setDestAddress("Ký túc xá khu A, ĐHQG TP.HCM");

            if ("Hoàn thành".equalsIgnoreCase(orderStatus) || "COMPLETED".equalsIgnoreCase(orderStatus)) {
                mockData.setStatus("COMPLETED");
            } else {
                mockData.setStatus("DELIVERING");
            }

            List<CartItem> cartItems = CartManager.getInstance().getLastOrder();
            List<OrderDetail.CartItem> items = new ArrayList<>();
            long subtotal = 0;
            
            if (cartItems != null && !cartItems.isEmpty()) {
                for (CartItem ci : cartItems) {
                    OrderDetail.CartItem item = new OrderDetail.CartItem();
                    item.itemName = ci.getMenuItem().getName();
                    item.note = ci.getToppingsString() != null ? ci.getToppingsString() : "";
                    item.price = ci.getTotalPrice() / ci.getQuantity(); // price per item
                    item.quantity = ci.getQuantity();
                    items.add(item);
                    subtotal += ci.getTotalPrice();
                }
            } else {
                OrderDetail.CartItem item1 = new OrderDetail.CartItem();
                item1.itemName = "Món ăn mặc định";
                item1.note = "Không có";
                item1.price = 58000;
                item1.quantity = 1;
                items.add(item1);
                subtotal = 58000;
            }
            mockData.setItems(items);
            mockData.setTotalPaid(subtotal + deliveryFee - discount + 3000); // 3000 is applying fee

            bindOrderData(mockData);
            
            // Format fee and discount on UI
            tvShipFee.setText(String.format("%,.0fđ", (double) deliveryFee));
            if (discount > 0) {
                tvDiscount.setText(String.format("-%,.0fđ", (double) discount));
                tvDiscount.setTextColor(android.graphics.Color.parseColor("#00B159"));
            } else {
                tvDiscount.setText("0đ");
                tvDiscount.setTextColor(android.graphics.Color.parseColor("#777777"));
            }
        }

        btnMapBack.setOnClickListener(v -> finish());
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void bindOrderData(OrderDetail data) {
        tvMerchantName.setText("Từ: " + data.getMerchantName());
        tvDestAddress.setText("Đến: " + data.getDestAddress());
        tvTotalPaid.setText(String.format("%,.0fđ", data.getTotalPaid()));
        tvDetailOrderId.setText(data.getOrderId());

        if (data.getItems() != null && !data.getItems().isEmpty()) {
            rvOrderItems.setAdapter(new DetailItemAdapter(data.getItems()));
            double subtotal = 0;
            int totalQty = 0;
            for (OrderDetail.CartItem item : data.getItems()) {
                subtotal += (item.price * item.quantity);
                totalQty += item.quantity;
            }
            tvTotalItemsTitle.setText("Tổng (" + totalQty + " món)");
            tvItemsSubtotal.setText(String.format("%,.0fđ", subtotal));
        }

        // Logic sao chép mã đơn hàng
        btnCopyOrderId.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("OrderId", data.getOrderId());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Đã sao chép mã đơn hàng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Logic nút hủy đơn hàng
        btnCancelOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Đang gửi yêu cầu hủy đơn " + data.getOrderId() + " lên hệ thống...", Toast.LENGTH_SHORT).show();
        });

        btnCallDriver.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + data.getCustomerPhone()));
            startActivity(intent);
        });

        btnChatDriver.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:+84987301126"));
            intent.putExtra("sms_body", "Xin chào shipper, đơn hàng " + data.getOrderId() + " của tôi thế nào rồi ạ?");
            startActivity(intent);
        });

        if ("COMPLETED".equalsIgnoreCase(data.getStatus())) {
            tvOrderStatusTitle.setText("Hoàn thành");
            tvOrderTimeHeader.setVisibility(View.GONE);
            layoutDriverInfo.setVisibility(View.GONE);
            mapContainer.setVisibility(View.GONE);
            btnCancelOrder.setVisibility(View.GONE);

            toolbar.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setDraggable(false);

        } else {
            tvOrderStatusTitle.setText("Đơn đang được giao...");
            tvOrderTimeHeader.setVisibility(View.VISIBLE);
            layoutDriverInfo.setVisibility(View.VISIBLE);
            mapContainer.setVisibility(View.VISIBLE);
            btnCancelOrder.setVisibility(View.VISIBLE);

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setDraggable(true);

            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        toolbar.setVisibility(View.VISIBLE);
                        btnMapBack.setVisibility(View.GONE);
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        toolbar.setVisibility(View.GONE);
                        btnMapBack.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setAlpha(slideOffset);
                    if (slideOffset == 0) toolbar.setVisibility(View.GONE);
                }
            });
        }
    }

    // Lớp Adapter nội bộ cập nhật theo class OrderDetail mới
    private static class DetailItemAdapter extends RecyclerView.Adapter<DetailItemAdapter.ViewHolder> {
        private final List<OrderDetail.CartItem> items;
        public DetailItemAdapter(List<OrderDetail.CartItem> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_order_detail_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrderDetail.CartItem item = items.get(position);
            holder.tvName.setText(item.quantity + " x " + item.itemName);
            holder.tvNote.setText(item.note);
            holder.tvPrice.setText(String.format("%,.0fđ", item.price));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvNote, tvPrice;
            ViewHolder(View view) {
                super(view);
                tvName = view.findViewById(R.id.tvProductQtyName);
                tvNote = view.findViewById(R.id.tvProductNote);
                tvPrice = view.findViewById(R.id.tvProductPrice);
            }
        }
    }
}