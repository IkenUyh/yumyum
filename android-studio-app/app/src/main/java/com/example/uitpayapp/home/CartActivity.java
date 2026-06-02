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
import com.example.uitpayapp.home.checkout.TransferConfirmationActivity;
import com.example.uitpayapp.home.home_adapters.CartAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartActionListener {

    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private TextView tvTotalPrice;
    private TextView tvCartBadge;
    private LinearLayout layoutEmptyCart;
    private RecyclerView rvCartItems;
    private LinearLayout layoutBottomBar;
    private NumberFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        // Tràn viền status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_cart);

        // Xử lý padding cho status bar
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

        // Ánh xạ view
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        layoutEmptyCart = findViewById(R.id.layout_empty_cart);
        rvCartItems = findViewById(R.id.rv_cart_items);
        layoutBottomBar = findViewById(R.id.layout_bottom_bar);

        // Nút quay lại
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Setup RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartManager.getCart(), this);
        rvCartItems.setAdapter(cartAdapter);

        // Nút thanh toán
        findViewById(R.id.btn_checkout).setOnClickListener(v -> checkout());

        // Cập nhật UI
        updateCartUI();
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
            tvCartBadge.setText(cartManager.getTotalItemCount() + " món");
        }
    }

    @Override
    public void onQuantityChanged() {
        updateCartUI();
    }

    @Override
    public void onRequestRemoveItem(int position, CartItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn muốn loại bỏ món \"" + item.getMenuItem().getName() + "\" khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    cartManager.removeItem(position);
                    cartAdapter.removeItem(position);
                    updateCartUI();
                    Toast.makeText(this, "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    // Giữ lại số lượng = 1
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private void checkout() {
        if (cartManager.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        long totalAmount = cartManager.getTotalPrice();
        String productNames = cartManager.getProductSummary();

        Intent intent = new Intent(this, TransferConfirmationActivity.class);
        intent.putExtra("KEY_AMOUNT", String.valueOf(totalAmount));
        intent.putExtra("KEY_IS_FOOD_ORDER", true);
        intent.putExtra("KEY_FOOD_PRODUCTS", productNames);
        intent.putExtra("KEY_FROM_CART", true);
        startActivity(intent);

        // Xóa giỏ hàng và quay về Home
        cartManager.clearCart();
        finish();
    }
}
