package com.example.uitpayapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import com.example.uitpayapp.home.home_adapters.CartAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartActionListener {

    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private TextView tvTotalPrice;
    private LinearLayout layoutEmptyCart;
    private RecyclerView rvCartItems;
    private LinearLayout layoutBottomBar;
    private NumberFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_cart_header), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + 16, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_bottom_bar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom + 12);
            return insets;
        });

        formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        cartManager = CartManager.getInstance();

        tvTotalPrice = findViewById(R.id.tv_total_price);
        layoutEmptyCart = findViewById(R.id.layout_empty_cart);
        rvCartItems = findViewById(R.id.rv_cart_items);
        layoutBottomBar = findViewById(R.id.layout_bottom_bar);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(new java.util.ArrayList<>(), this);
        rvCartItems.setAdapter(cartAdapter);

        findViewById(R.id.btn_checkout).setOnClickListener(v -> checkout());

        loadCartData();
    }

    private void loadCartData() {
        cartManager.fetchCartFromServer(new com.example.uitpayapp.network.ApiCallback<java.util.List<CartItem>>() {
            @Override
            public void onSuccess(java.util.List<CartItem> items) {
                runOnUiThread(() -> {
                    cartAdapter = new CartAdapter(items, CartActivity.this);
                    rvCartItems.setAdapter(cartAdapter);
                    updateCartUI();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                    updateCartUI();
                });
            }
        });
    }

    private void updateCartUI() {
        if (cartManager.isEmpty()) {
            rvCartItems.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE);
            layoutBottomBar.setVisibility(View.GONE);
        } else {
            rvCartItems.setVisibility(View.VISIBLE);
            layoutEmptyCart.setVisibility(View.GONE);
            layoutBottomBar.setVisibility(View.VISIBLE);
            tvTotalPrice.setText(cartManager.getFormattedTotalPrice());
        }
    }

    @Override
    public void onQuantityChanged(int position, int newQuantity) {
        cartManager.updateQuantitySync(position, newQuantity, new com.example.uitpayapp.network.ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> {
                    cartAdapter.notifyItemChanged(position);
                    updateCartUI();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(CartActivity.this, "Lỗi cập nhật số lượng: " + errorMessage, Toast.LENGTH_SHORT).show();
                    loadCartData();
                });
            }
        });
    }

    @Override
    public void onRequestRemoveItem(int position, CartItem item) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_confirm_delete);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
            dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(android.view.Gravity.CENTER);
        }

        android.widget.ImageView imgFood = dialog.findViewById(R.id.img_dialog_food);
        TextView tvFoodName = dialog.findViewById(R.id.tv_dialog_food_name);
        TextView btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);
        TextView btnDelete = dialog.findViewById(R.id.btn_dialog_delete);

        imgFood.setImageResource(item.getMenuItem().getImageResId());
        tvFoodName.setText(item.getMenuItem().getName());

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnDelete.setOnClickListener(v -> {
            cartManager.removeItemSync(position, new com.example.uitpayapp.network.ApiCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    runOnUiThread(() -> {
                        cartAdapter.removeItem(position);
                        updateCartUI();
                        showCustomSnackbar("Đã xóa khỏi giỏ hàng");
                        dialog.dismiss();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(CartActivity.this, "Lỗi khi xóa món: " + errorMessage, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
            });
        });

        dialog.show();
    }

    private void checkout() {
        if (cartManager.isEmpty()) {
            showCustomSnackbar("Giỏ hàng của bạn đang trống!");
            return;
        }

        com.example.uitpayapp.network.SessionManager session = com.example.uitpayapp.network.SessionManager.getInstance(this);
        Double userLat = session.getDeliveryLatitude();
        Double userLng = session.getDeliveryLongitude();

        if (userLat == null || userLng == null) {
            new AlertDialog.Builder(this)
                .setTitle("Thiếu địa chỉ")
                .setMessage("Vui lòng chọn địa chỉ giao hàng ở Trang Chủ trước khi tiến hành thanh toán.")
                .setPositiveButton("Đóng", null)
                .show();
            return;
        }

        CartItem firstItem = cartManager.getCart().get(0);
        Double restLat = firstItem.getMenuItem().getRestaurantLatitude();
        Double restLng = firstItem.getMenuItem().getRestaurantLongitude();

        if (restLat != null && restLng != null) {
            float[] results = new float[1];
            android.location.Location.distanceBetween(userLat, userLng, restLat, restLng, results);
            double distanceKm = results[0] / 1000.0;

            if (distanceKm > 15.0) {
                new AlertDialog.Builder(this)
                    .setTitle("Quá xa để giao hàng")
                    .setMessage(String.format(java.util.Locale.getDefault(), "Khoảng cách hiện tại đến cửa hàng là %.1f km, vượt quá giới hạn giao hàng tối đa (15km).", distanceKm))
                    .setPositiveButton("Đóng", null)
                    .show();
                return;
            }
        }

        Intent intent = new Intent(this, com.example.uitpayapp.home.checkout.FoodCheckoutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEditItemClick(int position, CartItem item) {
        showEditFoodPopup(item, position);
    }

    private void showEditFoodPopup(CartItem item, int position) {
        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(this, item.getMenuItem(), item, (selectedItem, quantity, selectedToppings) -> {
            CartItem updatedItem = new CartItem(item.getDbId(), selectedItem, quantity, new java.util.ArrayList<>(selectedToppings));
            cartManager.updateItemSync(position, updatedItem, new com.example.uitpayapp.network.ApiCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    runOnUiThread(() -> {
                        cartAdapter = new CartAdapter(cartManager.getCart(), CartActivity.this);
                        rvCartItems.setAdapter(cartAdapter);
                        updateCartUI();
                        showCustomSnackbar("Cập nhật thành công");
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(CartActivity.this, "Lỗi cập nhật: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    private void showCustomSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(rootView, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT);
        
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(android.graphics.Color.WHITE);
        
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (textView != null) {
            textView.setTextColor(android.graphics.Color.BLACK);
        }
        
        snackbar.show();
    }
}