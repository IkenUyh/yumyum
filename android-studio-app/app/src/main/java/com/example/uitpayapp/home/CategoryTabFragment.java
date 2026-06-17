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

        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(getContext(), item, null, (selectedItem, quantity, selectedToppings) -> {
            CartManager.getInstance().addItem(new CartItem(selectedItem, quantity, selectedToppings));

            // Fly-to-cart animation giống Home
            if (getActivity() != null) {
                View btnCart = getActivity().findViewById(R.id.btn_cart);
                if (btnCart != null) {
                    CartAnimationHelper.animateFlyToCart(getActivity(), sourceImage != null ? sourceImage : getActivity().findViewById(android.R.id.content), btnCart, () -> {
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
        });
    }
}
