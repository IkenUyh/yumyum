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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.CartItem;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    // 1. Hệ thống điều khiển & Giao diện trượt
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private View mapContainer;
    private View layoutDriverInfo;
    private Toolbar toolbar;
    private GoogleMap mMap;
    private com.example.uitpayapp.modules.order.OrderRepository orderRepository;
    private double merchantLat = 10.8800;
    private double merchantLng = 106.8000;
    private double customerLat = 10.8750;
    private double customerLng = 106.7900;
    private boolean isMapDataLoaded = false;
    private String orderStatus;
    private Marker shipperMarker;
    private ValueAnimator shipperAnimator;
    private static final Map<String, Long> deliveryStartTimes = new HashMap<>();

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
    private TextView tvCustomerContact;

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
        
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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
        tvCustomerContact = findViewById(R.id.tvCustomerContact);

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

        orderRepository = new com.example.uitpayapp.modules.order.OrderRepository();

        Intent intent = getIntent();
        if (intent != null) {
            String orderIdStr = intent.getStringExtra("ORDER_ID");
            if (orderIdStr != null) {
                try {
                    long id = Long.parseLong(orderIdStr);
                    fetchOrderDetail(id);
                } catch (NumberFormatException e) {
                    OrderDetail fallback = new OrderDetail();
                    fallback.setOrderId(orderIdStr);
                    fallback.setCustomerName("Khách Hàng");
                    fallback.setCustomerPhone("+84987301126");
                    fallback.setMerchantName("UIT FOOD");
                    fallback.setDestAddress("Ký túc xá khu A, ĐHQG TP.HCM");
                    orderStatus = "COMPLETED";
                    fallback.setStatus(orderStatus);

                    List<OrderDetail.CartItem> items = new ArrayList<>();
                    OrderDetail.CartItem item1 = new OrderDetail.CartItem();
                    item1.itemName = "Món ăn mặc định";
                    item1.note = "Không có";
                    item1.price = 58000;
                    item1.quantity = 1;
                    items.add(item1);
                    fallback.setItems(items);
                    fallback.setShippingFee(15000);
                    fallback.setDiscount(0);
                    fallback.setTotalPaid(58000 + 15000 - 0 + 3000);
                    bindOrderData(fallback);
                }
            } else {
                Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        btnMapBack.setOnClickListener(v -> finish());
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void fetchOrderDetail(long orderId) {
        orderRepository.getOrderById(orderId, new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.order.models.responses.OrderResponse>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.order.models.responses.OrderResponse order) {
                runOnUiThread(() -> {
                    OrderDetail data = new OrderDetail();
                    data.setOrderId(String.valueOf(order.getId()));
                    data.setMerchantName(order.getRestaurantName() != null ? order.getRestaurantName() : "UIT FOOD");
                    data.setDestAddress(order.getDestAddress() != null ? order.getDestAddress() : "Ký túc xá khu A, ĐHQG TP.HCM");
                    data.setCustomerName(order.getCustomerName() != null ? order.getCustomerName() : "Khách Hàng");
                    data.setCustomerPhone(order.getCustomerPhone() != null ? order.getCustomerPhone() : "+84987301126");
                    data.setPaymentMethod(order.getPaymentMethod());
                    data.setOrderNote(order.getNote());
                    data.setOrderTime(order.getCreatedAt());
                    orderStatus = order.getStatus();
                    data.setStatus(orderStatus);

                    // Đọc tọa độ thực tế từ backend
                    merchantLat = order.getRestaurantLatitude() != null ? order.getRestaurantLatitude().doubleValue() : 10.8800;
                    merchantLng = order.getRestaurantLongitude() != null ? order.getRestaurantLongitude().doubleValue() : 106.8000;
                    customerLat = order.getDestLatitude() != null ? order.getDestLatitude().doubleValue() : 10.8750;
                    customerLng = order.getDestLongitude() != null ? order.getDestLongitude().doubleValue() : 106.7900;
                    isMapDataLoaded = true;
                    if (mMap != null) {
                        updateMapMarkers();
                    }

                    List<OrderDetail.CartItem> items = new ArrayList<>();
                    long subtotal = 0;
                    if (order.getItems() != null) {
                        for (com.example.uitpayapp.modules.order.models.responses.OrderResponse.OrderItemResponse item : order.getItems()) {
                            OrderDetail.CartItem ci = new OrderDetail.CartItem();
                            ci.itemName = item.getName();
                            ci.note = "";
                            ci.price = item.getPrice() != null ? item.getPrice().doubleValue() : 0;
                            ci.quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                            ci.imageUrl = item.getImageUrl();
                            items.add(ci);
                            subtotal += (ci.price * ci.quantity);
                        }
                    }
                    data.setItems(items);
                    data.setSubTotal(subtotal);

                    double ship = order.getShippingFee() != null ? order.getShippingFee().doubleValue() : 15000;
                    double disc = order.getDiscountAmount() != null ? order.getDiscountAmount().doubleValue() : 0;
                    data.setShippingFee(ship);
                    data.setDiscount(disc);
                    data.setTotalPaid(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : (subtotal + ship - disc + 3000));

                    bindOrderData(data);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderDetailActivity.this, "Lỗi tải chi tiết đơn hàng: " + errorMessage, Toast.LENGTH_LONG).show();
                    OrderDetail fallback = new OrderDetail();
                    fallback.setOrderId(String.valueOf(orderId));
                    fallback.setCustomerName("Khách Hàng");
                    fallback.setCustomerPhone("+84987301126");
                    fallback.setMerchantName("UIT FOOD");
                    fallback.setDestAddress("Ký túc xá khu A, ĐHQG TP.HCM");
                    orderStatus = "COMPLETED";
                    fallback.setStatus(orderStatus);

                    List<OrderDetail.CartItem> items = new ArrayList<>();
                    OrderDetail.CartItem item1 = new OrderDetail.CartItem();
                    item1.itemName = "Món ăn mặc định";
                    item1.note = "Không có";
                    item1.price = 58000;
                    item1.quantity = 1;
                    items.add(item1);
                    fallback.setItems(items);
                    fallback.setShippingFee(15000);
                    fallback.setDiscount(0);
                    fallback.setTotalPaid(58000 + 15000 - 0 + 3000);
                    bindOrderData(fallback);
                });
            }
        });
    }

    private void bindOrderData(OrderDetail data) {
        orderStatus = data.getStatus();
        tvMerchantName.setText("Từ: " + data.getMerchantName());
        tvDestAddress.setText("Đến: " + data.getDestAddress());
        if (tvCustomerContact != null) {
            String contactName = data.getCustomerName() != null ? data.getCustomerName() : "";
            String contactPhone = data.getCustomerPhone() != null ? data.getCustomerPhone() : "";
            if (!contactName.isEmpty() && !contactPhone.isEmpty()) {
                tvCustomerContact.setText(contactName + " - " + contactPhone);
            } else if (!contactName.isEmpty()) {
                tvCustomerContact.setText(contactName);
            } else {
                tvCustomerContact.setText(contactPhone);
            }
        }
        tvTotalPaid.setText(String.format("%,.0fđ", data.getTotalPaid()));
        tvDetailOrderId.setText(data.getOrderId());
        tvOrderPlacedTime.setText(data.getOrderTime() != null ? data.getOrderTime() : "Không có thông tin");
        tvOrderNote.setText(data.getOrderNote() != null && !data.getOrderNote().trim().isEmpty() ? data.getOrderNote() : "Không có");

        if ("ZALOPAY".equalsIgnoreCase(data.getPaymentMethod())) {
            tvPaymentMethod.setText("ZaloPay");
        } else if ("CASH".equalsIgnoreCase(data.getPaymentMethod())) {
            tvPaymentMethod.setText("Tiền mặt");
        } else {
            tvPaymentMethod.setText("Ví nội bộ (YumYumPay)");
        }

        tvShipFee.setText(String.format("%,.0fđ", data.getShippingFee()));
        if (data.getDiscount() > 0) {
            tvDiscount.setText(String.format("-%,.0fđ", data.getDiscount()));
            tvDiscount.setTextColor(android.graphics.Color.parseColor("#00B159"));
        } else {
            tvDiscount.setText("0đ");
            tvDiscount.setTextColor(android.graphics.Color.parseColor("#777777"));
        }

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
            com.example.uitpayapp.modules.order.models.requests.CancelOrderRequest request = 
                new com.example.uitpayapp.modules.order.models.requests.CancelOrderRequest("Khách hàng đổi ý");

            com.example.uitpayapp.modules.order.OrderRepository orderRepo = new com.example.uitpayapp.modules.order.OrderRepository();
            // Lấy ID đơn hàng, tạm parse từ String (nếu hệ thống BE dùng Long, bạn cần đảm bảo ORDER_ID là số)
            long orderIdLong = 1L; // Mock vì data.getOrderId() hiện là chuỗi format "05066-..."
            try { orderIdLong = Long.parseLong(data.getOrderId()); } catch(Exception ignored) {}

            orderRepo.cancelOrder(orderIdLong, request, new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.order.models.responses.OrderResponse>() {
                @Override
                public void onSuccess(com.example.uitpayapp.modules.order.models.responses.OrderResponse res) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderDetailActivity.this, "Hủy đơn thành công!", Toast.LENGTH_SHORT).show();
                        data.setStatus("CANCELLED");
                        bindOrderData(data); // Cập nhật lại UI
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderDetailActivity.this, "Lỗi hủy đơn: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        btnCallDriver.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + data.getCustomerPhone()));
            startActivity(intent);
        });

        btnChatDriver.setOnClickListener(v -> {
            // Chuyển màn hình trực tiếp sang Activity phòng chat mới lập
            Intent chatIntent = new Intent(OrderDetailActivity.this, ChatActivity.class);

            // Truyền đính kèm mã đơn và tên quán sang để hiển thị linh hoạt trên Toolbar
            chatIntent.putExtra("ORDER_ID", data.getOrderId());
            chatIntent.putExtra("MERCHANT_NAME", data.getMerchantName());
            
            // Xác định xem phòng chat có bị khóa hay không
            boolean isLocked = "COMPLETED".equalsIgnoreCase(data.getStatus()) || "CANCELLED".equalsIgnoreCase(data.getStatus());
            chatIntent.putExtra("IS_CHAT_LOCKED", isLocked);

            startActivity(chatIntent);
        });

        if ("COMPLETED".equalsIgnoreCase(data.getStatus()) || "CANCELLED".equalsIgnoreCase(data.getStatus())) {
            if (shipperAnimator != null) {
                shipperAnimator.cancel();
            }
            if (shipperMarker != null) {
                shipperMarker.remove();
            }
            if ("CANCELLED".equalsIgnoreCase(data.getStatus())) {
                String refundInfo = "ZALOPAY".equalsIgnoreCase(data.getPaymentMethod()) 
                    ? "Đã hủy (Đã hoàn tiền ZaloPay)" 
                    : "Đã hủy (Đã hoàn lại Ví YumYumPay)";
                tvOrderStatusTitle.setText(refundInfo);
                findViewById(R.id.layoutDeliveryProgress).setVisibility(View.GONE);
            } else {
                tvOrderStatusTitle.setText("Hoàn thành");
                updateProgressState(4);
            }
            tvOrderTimeHeader.setVisibility(View.GONE);
            layoutDriverInfo.setVisibility(View.GONE);
            mapContainer.setVisibility(View.GONE);
            btnCancelOrder.setVisibility(View.GONE);

            toolbar.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setDraggable(false);

        } else {
            String statusStr = data.getStatus();
            if ("PENDING".equalsIgnoreCase(statusStr)) {
                tvOrderStatusTitle.setText("Chờ xác nhận...");
                btnCancelOrder.setVisibility(View.VISIBLE);
                updateProgressState(1);
            } else if ("PREPARING".equalsIgnoreCase(statusStr)) {
                tvOrderStatusTitle.setText("Đang chuẩn bị...");
                btnCancelOrder.setVisibility(View.GONE);
                updateProgressState(2);
            } else {
                tvOrderStatusTitle.setText("Đơn đang được giao...");
                btnCancelOrder.setVisibility(View.GONE);
                updateProgressState(3);
            }
            tvOrderTimeHeader.setVisibility(View.VISIBLE);
            layoutDriverInfo.setVisibility(View.VISIBLE);
            mapContainer.setVisibility(View.VISIBLE);

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

    private void updateProgressState(int step) {
        ImageView iv1 = findViewById(R.id.ivProgressStep1);
        View line1 = findViewById(R.id.lineProgress1);
        TextView tv1 = findViewById(R.id.tvProgressStep1);
        ImageView iv2 = findViewById(R.id.ivProgressStep2);
        View line2 = findViewById(R.id.lineProgress2);
        TextView tv2 = findViewById(R.id.tvProgressStep2);
        ImageView iv3 = findViewById(R.id.ivProgressStep3);
        View line3 = findViewById(R.id.lineProgress3);
        TextView tv3 = findViewById(R.id.tvProgressStep3);
        ImageView iv4 = findViewById(R.id.ivProgressStep4);
        TextView tv4 = findViewById(R.id.tvProgressStep4);

        if (iv1 == null) return;

        int activeColor = android.graphics.Color.parseColor("#FF5722");
        int inactiveColor = android.graphics.Color.parseColor("#E0E0E0");
        int inactiveTextColor = android.graphics.Color.parseColor("#757575");

        // Step 1
        iv1.setImageResource(R.drawable.ic_circle_check);
        iv1.setColorFilter(activeColor);
        tv1.setTextColor(activeColor);
        tv1.setTypeface(null, android.graphics.Typeface.BOLD);

        // Step 2
        if (step >= 2) {
            line1.setBackgroundColor(activeColor);
            iv2.setImageResource(R.drawable.ic_circle_check);
            iv2.setColorFilter(activeColor);
            tv2.setTextColor(activeColor);
            tv2.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            line1.setBackgroundColor(inactiveColor);
            iv2.setImageResource(R.drawable.bg_circle_gray);
            iv2.clearColorFilter();
            tv2.setTextColor(inactiveTextColor);
            tv2.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        // Step 3
        if (step >= 3) {
            line2.setBackgroundColor(activeColor);
            iv3.setImageResource(R.drawable.ic_circle_check);
            iv3.setColorFilter(activeColor);
            tv3.setTextColor(activeColor);
            tv3.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            line2.setBackgroundColor(inactiveColor);
            iv3.setImageResource(R.drawable.bg_circle_gray);
            iv3.clearColorFilter();
            tv3.setTextColor(inactiveTextColor);
            tv3.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        // Step 4
        if (step >= 4) {
            line3.setBackgroundColor(activeColor);
            iv4.setImageResource(R.drawable.ic_circle_check);
            iv4.setColorFilter(activeColor);
            tv4.setTextColor(activeColor);
            tv4.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            line3.setBackgroundColor(inactiveColor);
            iv4.setImageResource(R.drawable.bg_circle_gray);
            iv4.clearColorFilter();
            tv4.setTextColor(inactiveTextColor);
            tv4.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        mMap.clear();
        drawMarkers(merchantLat, merchantLng, customerLat, customerLng);
    }

    private void drawMarkers(double mLat, double mLng, double cLat, double cLng) {
        LatLng merchantLocation = new LatLng(mLat, mLng);
        LatLng customerLocation = new LatLng(cLat, cLng);

        mMap.addMarker(new MarkerOptions().position(merchantLocation).title("Quán"));
        mMap.addMarker(new MarkerOptions().position(customerLocation).title("Khách hàng"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(merchantLocation);
        builder.include(customerLocation);
        LatLngBounds bounds = builder.build();

        mMap.setOnMapLoadedCallback(() -> {
            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(merchantLocation)
                .add(customerLocation)
                .width(8)
                .color(android.graphics.Color.parseColor("#E84A26"));
        mMap.addPolyline(polylineOptions);

        if ("DELIVERING".equalsIgnoreCase(orderStatus)) {
            startShipperSimulation(merchantLocation, customerLocation);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (isMapDataLoaded) {
            updateMapMarkers();
        } else {
            drawMarkers(10.8800, 106.8000, 10.8750, 106.7900);
        }
    }

    @Override
    protected void onDestroy() {
        if (shipperAnimator != null) {
            shipperAnimator.cancel();
        }
        super.onDestroy();
    }

    private BitmapDescriptor getMarkerIconFromDrawable(int resId, int width, int height) {
        android.graphics.drawable.Drawable drawable = androidx.core.content.ContextCompat.getDrawable(this, resId);
        if (drawable == null) {
            return BitmapDescriptorFactory.defaultMarker();
        }
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat1 = Math.toRadians(begin.latitude);
        double lng1 = Math.toRadians(begin.longitude);
        double lat2 = Math.toRadians(end.latitude);
        double lng2 = Math.toRadians(end.longitude);

        double dLng = lng2 - lng1;

        double y = Math.sin(dLng) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (float) (bearing + 360) % 360;
    }

    private void startShipperSimulation(LatLng merchantLocation, LatLng customerLocation) {
        if (shipperAnimator != null) {
            shipperAnimator.cancel();
        }
        if (shipperMarker != null) {
            shipperMarker.remove();
        }

        shipperMarker = mMap.addMarker(new MarkerOptions()
                .position(merchantLocation)
                .title("Shipper đang giao hàng")
                .anchor(0.5f, 0.5f)
                .icon(getMarkerIconFromDrawable(R.drawable.ic_shipper, 100, 100)));

        long totalDurationMs = 5 * 60 * 1000; // 5 phút giả lập
        long now = System.currentTimeMillis();
        String orderId = tvDetailOrderId.getText().toString();

        Long startTime = deliveryStartTimes.get(orderId);
        if (startTime == null) {
            startTime = now;
            deliveryStartTimes.put(orderId, startTime);
        }

        long elapsed = now - startTime;
        if (elapsed >= totalDurationMs) {
            shipperMarker.setPosition(customerLocation);
            float bearing = getBearing(merchantLocation, customerLocation);
            shipperMarker.setRotation(bearing);
            return;
        }

        float startFraction = (float) elapsed / totalDurationMs;
        long remainingDuration = totalDurationMs - elapsed;

        shipperAnimator = ValueAnimator.ofFloat(startFraction, 1.0f);
        shipperAnimator.setDuration(remainingDuration);
        shipperAnimator.setInterpolator(new LinearInterpolator());
        shipperAnimator.addUpdateListener(animation -> {
            if (mMap == null || shipperMarker == null) return;
            float fraction = (float) animation.getAnimatedValue();

            double lat = merchantLocation.latitude + (customerLocation.latitude - merchantLocation.latitude) * fraction;
            double lng = merchantLocation.longitude + (customerLocation.longitude - merchantLocation.longitude) * fraction;
            LatLng currentPos = new LatLng(lat, lng);

            shipperMarker.setPosition(currentPos);

            float bearing = getBearing(merchantLocation, customerLocation);
            shipperMarker.setRotation(bearing);
        });

        shipperAnimator.start();
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

            if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(item.imageUrl)
                        .into(holder.ivProductThumb);
            }
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvNote, tvPrice;
            com.google.android.material.imageview.ShapeableImageView ivProductThumb;
            ViewHolder(View view) {
                super(view);
                tvName = view.findViewById(R.id.tvProductQtyName);
                tvNote = view.findViewById(R.id.tvProductNote);
                tvPrice = view.findViewById(R.id.tvProductPrice);
                ivProductThumb = view.findViewById(R.id.ivProductThumb);
            }
        }
    }
}
