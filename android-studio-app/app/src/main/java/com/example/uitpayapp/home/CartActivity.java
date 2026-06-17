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
        cartAdapter = new CartAdapter(cartManager.getCart(), this);
        rvCartItems.setAdapter(cartAdapter);

        findViewById(R.id.btn_checkout).setOnClickListener(v -> checkout());

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
        }
    }

    @Override
    public void onQuantityChanged() {
        updateCartUI();
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
            cartManager.removeItem(position);
            cartAdapter.removeItem(position);
            updateCartUI();
            showCustomSnackbar("Đã xóa khỏi giỏ hàng");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void checkout() {
        if (cartManager.isEmpty()) {
            showCustomSnackbar("Giỏ hàng của bạn đang trống!");
            return;
        }

        long totalAmount = cartManager.getTotalPrice();
        String productNames = cartManager.getProductSummary();

        Intent intent = new Intent(this, com.example.uitpayapp.home.checkout.FoodCheckoutActivity.class);
        startActivity(intent);

        // We do NOT clear cart here because checkout might be cancelled.
        // Cart will be cleared upon successful confirmation inside FoodCheckoutActivity.
        // We also do not finish() so that the back button returns to the CartActivity.
    }

    @Override
    public void onEditItemClick(int position, CartItem item) {
        showEditFoodPopup(item, position);
    }

    private void showEditFoodPopup(CartItem item, int position) {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_food_detail, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        android.widget.ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        android.widget.ImageView ivFoodImage = view.findViewById(R.id.iv_food_image);
        TextView tvFoodName = view.findViewById(R.id.tv_food_name);
        TextView tvFoodDesc = view.findViewById(R.id.tv_food_desc);
        TextView tvFoodPrice = view.findViewById(R.id.tv_food_price);

        ivFoodImage.setImageResource(item.getMenuItem().getImageResId());
        tvFoodName.setText(item.getMenuItem().getName());
        tvFoodDesc.setText(item.getMenuItem().getDescription());
        tvFoodPrice.setText(item.getMenuItem().getFormattedPrice());

        final int[] popupQty = {item.getQuantity()};
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        View btnDecrease = view.findViewById(R.id.btn_decrease);
        View btnIncrease = view.findViewById(R.id.btn_increase);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        
        tvQuantity.setText(String.valueOf(popupQty[0]));

        final int[] toppingTotal = {0};

        // Add mock toppings
        LinearLayout layoutToppings = view.findViewById(R.id.layout_toppings_container);
        String[] mockToppings = {"Thêm trân châu đen", "Thêm phô mai", "Thêm thạch mảng cầu", "Không đá", "Ít đường"};
        int[] mockPrices = {5000, 10000, 5000, 0, 0};

        final java.util.List<com.example.uitpayapp.home.home_models.CartTopping> selectedToppings = new java.util.ArrayList<>();
        if (item.getSelectedToppings() != null) {
            selectedToppings.addAll(item.getSelectedToppings());
            for (com.example.uitpayapp.home.home_models.CartTopping t : selectedToppings) {
                toppingTotal[0] += t.getPrice();
            }
        }

        for (int i = 0; i < 5; i++) {
            View toppingView = android.view.LayoutInflater.from(this).inflate(R.layout.item_food_topping, layoutToppings, false);
            android.widget.CheckBox cbTopping = toppingView.findViewById(R.id.cb_topping);
            TextView tvToppingPrice = toppingView.findViewById(R.id.tv_topping_price);
            cbTopping.setText(mockToppings[i]);
            
            if (mockPrices[i] > 0) {
                tvToppingPrice.setText("+" + String.format("%,dđ", mockPrices[i]).replace(',', '.'));
            } else {
                tvToppingPrice.setText("0đ");
            }
            
            final int price = mockPrices[i];
            final String toppingName = mockToppings[i];
            final String toppingId = "tp_" + i;
            
            boolean isPreSelected = false;
            for (com.example.uitpayapp.home.home_models.CartTopping t : selectedToppings) {
                if (t.getName().equals(toppingName)) {
                    isPreSelected = true;
                    break;
                }
            }
            cbTopping.setChecked(isPreSelected);
            
            cbTopping.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    toppingTotal[0] += price;
                    selectedToppings.add(new com.example.uitpayapp.home.home_models.CartTopping(toppingId, toppingName, price));
                } else {
                    toppingTotal[0] -= price;
                    selectedToppings.remove(new com.example.uitpayapp.home.home_models.CartTopping(toppingId, toppingName, price));
                }
                updatePopupPrice(view, item.getMenuItem().getPrice(), toppingTotal[0]);
            });
            
            layoutToppings.addView(toppingView);
        }

        updatePopupPrice(view, item.getMenuItem().getPrice(), toppingTotal[0]);

        btnDecrease.setOnClickListener(v -> {
            if (popupQty[0] > 1) {
                popupQty[0]--;
                tvQuantity.setText(String.valueOf(popupQty[0]));
                updatePopupPrice(view, item.getMenuItem().getPrice(), toppingTotal[0]);
            }
        });

        btnIncrease.setOnClickListener(v -> {
            popupQty[0]++;
            tvQuantity.setText(String.valueOf(popupQty[0]));
            updatePopupPrice(view, item.getMenuItem().getPrice(), toppingTotal[0]);
        });

        btnAddToCart.setOnClickListener(v -> {
            CartItem updatedItem = new CartItem(item.getMenuItem(), popupQty[0], new java.util.ArrayList<>(selectedToppings));
            cartManager.updateItem(position, updatedItem);
            cartAdapter.notifyDataSetChanged();
            updateCartUI();
            dialog.dismiss();
            showCustomSnackbar("Cập nhật thành công");
        });

        dialog.show();
    }

    private void updatePopupPrice(View view, long itemPrice, int toppingTotal) {
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        int qty = Integer.parseInt(tvQuantity.getText().toString());
        long total = (itemPrice + toppingTotal) * qty;
        btnAddToCart.setText("Cập nhật - " + String.format("%,dđ", total).replace(',', '.'));
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