package com.example.uitpayapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_adapters.CategoryFoodAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.CartTopping;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.utils.CartAnimationHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class CategoryTabFragment extends Fragment {

    private static final String ARG_CATEGORY_NAME = "category_name";
    private static final String ARG_FILTER_TYPE = "filter_type";

    public static CategoryTabFragment newInstance(String categoryName, String filterType) {
        CategoryTabFragment fragment = new CategoryTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_NAME, categoryName);
        args.putString(ARG_FILTER_TYPE, filterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_tab, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_category_foods);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String categoryName = getArguments() != null ? getArguments().getString(ARG_CATEGORY_NAME) : "";
        String filterType = getArguments() != null ? getArguments().getString(ARG_FILTER_TYPE) : "";
        
        List<FoodMenuItem> foods = HomeActivity.HomeRepository.getInstance().getCategoryFoodsByName(categoryName);
        
        // Mock filter logic
        if ("Bán chạy".equals(filterType) || "Đánh giá".equals(filterType)) {
            java.util.Collections.shuffle(foods);
        }

        CategoryFoodAdapter adapter = new CategoryFoodAdapter(foods, (item, imageView) -> {
            showFoodItemDetailPopup(item, imageView);
        });
        recyclerView.setAdapter(adapter);

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_category);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                List<FoodMenuItem> refreshedFoods = HomeActivity.HomeRepository.getInstance().getCategoryFoodsByName(categoryName);
                if ("Bán chạy".equals(filterType) || "Đánh giá".equals(filterType)) {
                    java.util.Collections.shuffle(refreshedFoods);
                }
                CategoryFoodAdapter newAdapter = new CategoryFoodAdapter(refreshedFoods, (item, imageView) -> {
                    showFoodItemDetailPopup(item, imageView);
                });
                recyclerView.setAdapter(newAdapter);
                swipeRefreshLayout.setRefreshing(false);
            });
        }

        return view;
    }

    private void showFoodItemDetailPopup(FoodMenuItem item, ImageView sourceImage) {
        if (getContext() == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_food_detail, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        ImageView ivFoodImage = view.findViewById(R.id.iv_food_image);
        TextView tvFoodName = view.findViewById(R.id.tv_food_name);
        TextView tvFoodDesc = view.findViewById(R.id.tv_food_desc);
        TextView tvFoodPrice = view.findViewById(R.id.tv_food_price);

        ivFoodImage.setImageResource(item.getImageResId());
        tvFoodName.setText(item.getName());
        tvFoodDesc.setText(item.getDescription());
        tvFoodPrice.setText(item.getFormattedPrice());

        final int[] popupQty = {1};
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        View btnDecrease = view.findViewById(R.id.btn_decrease);
        View btnIncrease = view.findViewById(R.id.btn_increase);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);

        final int[] toppingTotal = {0};
        updatePopupPrice(view, item.getPrice(), toppingTotal[0]);

        btnDecrease.setOnClickListener(v -> {
            if (popupQty[0] > 1) {
                popupQty[0]--;
                tvQuantity.setText(String.valueOf(popupQty[0]));
                updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
            }
        });

        btnIncrease.setOnClickListener(v -> {
            popupQty[0]++;
            tvQuantity.setText(String.valueOf(popupQty[0]));
            updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
        });

        // Mock toppings
        LinearLayout layoutToppings = view.findViewById(R.id.layout_toppings_container);
        String[] mockToppings = {"Thêm trân châu đen", "Thêm phô mai", "Thêm thạch mảng cầu"};
        int[] mockPrices = {5000, 10000, 5000};
        final List<CartTopping> selectedToppings = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            View toppingView = LayoutInflater.from(getContext()).inflate(R.layout.item_food_topping, layoutToppings, false);
            CheckBox cbTopping = toppingView.findViewById(R.id.cb_topping);
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
            cbTopping.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    toppingTotal[0] += price;
                    selectedToppings.add(new CartTopping(toppingId, toppingName, price));
                } else {
                    toppingTotal[0] -= price;
                    selectedToppings.remove(new CartTopping(toppingId, toppingName, price));
                }
                updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
            });

            layoutToppings.addView(toppingView);
        }

        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addItem(new CartItem(item, popupQty[0], new ArrayList<>(selectedToppings)));

            // Fly-to-cart animation giống Home
            if (getActivity() != null) {
                View btnCart = getActivity().findViewById(R.id.btn_cart);
                if (btnCart != null) {
                    CartAnimationHelper.animateFlyToCart(getActivity(), ivFoodImage, btnCart, () -> {
                        // Update cart badge
                        if (getActivity() instanceof CategoryActivity) {
                            TextView tvBadge = getActivity().findViewById(R.id.tv_global_cart_badge);
                            int count = CartManager.getInstance().getTotalItemCount();
                            if (count > 0) {
                                tvBadge.setVisibility(View.VISIBLE);
                                tvBadge.setText(String.valueOf(count));
                            }
                        }
                    });
                }
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void updatePopupPrice(View view, long itemPrice, int toppingTotal) {
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        int qty = Integer.parseInt(tvQuantity.getText().toString());
        long total = (itemPrice + toppingTotal) * qty;
        btnAddToCart.setText("Thêm vào giỏ - " + String.format("%,dđ", total).replace(',', '.'));
    }
}
