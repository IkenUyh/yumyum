package com.example.uitpayapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_adapters.CategoryFoodAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.utils.CartAnimationHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryTabFragment extends Fragment {

    private static final String ARG_CATEGORY_NAME = "category_name";
    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_FILTER_TYPE = "filter_type";

    private CategoryViewModel viewModel;
    private RecyclerView recyclerView;
    private View layoutLoading, layoutError, layoutEmpty, swipeRefreshLayout;
    private TextView tvError, tvEmpty;

    public static CategoryTabFragment newInstance(String categoryName, long categoryId, String filterType) {
        CategoryTabFragment fragment = new CategoryTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_NAME, categoryName);
        args.putLong(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_FILTER_TYPE, filterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_tab, container, false);

        recyclerView = view.findViewById(R.id.rv_category_foods);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutLoading = view.findViewById(R.id.layout_category_loading);
        layoutError = view.findViewById(R.id.layout_category_error);
        layoutEmpty = view.findViewById(R.id.layout_category_empty);
        tvError = view.findViewById(R.id.tv_category_error);
        tvEmpty = view.findViewById(R.id.tv_category_empty);

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout srl = view.findViewById(R.id.swipe_refresh_category);
        swipeRefreshLayout = srl;

        long categoryId = getArguments() != null ? getArguments().getLong(ARG_CATEGORY_ID, -1L) : -1L;
        String filterType = getArguments() != null ? getArguments().getString(ARG_FILTER_TYPE) : "";

        // ViewModel được chia sẻ giữa các tab trong cùng 1 Activity
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        // Retry button
        View btnRetry = view.findViewById(R.id.btn_category_retry);
        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                if (categoryId > 0) {
                    viewModel.fetchFoodsByCategory(categoryId);
                }
            });
        }

        // Pull-to-refresh
        if (srl != null) {
            srl.setOnRefreshListener(() -> {
                if (categoryId > 0) {
                    viewModel.fetchFoodsByCategory(categoryId);
                }
                srl.setRefreshing(false);
            });
        }

        // Observe LiveData
        viewModel.getFoodsData().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                layoutLoading.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
            } else if (state.isSuccess()) {
                layoutLoading.setVisibility(View.GONE);
                layoutError.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);

                // Áp dụng filter cục bộ trước khi hiển thị
                List<FoodMenuItem> foods = state.getData();
                List<FoodMenuItem> displayFoods = foods != null ? new ArrayList<>(foods) : new ArrayList<>();

                // Sắp xếp cục bộ theo filterType
                if ("Bán chạy".equals(filterType) || "Đánh giá".equals(filterType)) {
                    java.util.Collections.shuffle(displayFoods);
                }

                CategoryFoodAdapter adapter = new CategoryFoodAdapter(displayFoods, (item, imageView) -> {
                    showFoodItemDetailPopup(item, imageView);
                });
                recyclerView.setAdapter(adapter);
            } else if (state.isEmpty()) {
                layoutLoading.setVisibility(View.GONE);
                layoutError.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                tvEmpty.setText("Chưa có món ăn nào trong danh mục này");
            } else if (state.isError()) {
                layoutLoading.setVisibility(View.GONE);
                layoutError.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                tvError.setText(state.getMessage() != null ? state.getMessage() : "Đã xảy ra lỗi");
            }
        });

        // Chỉ gọi API nếu ViewModel chưa có dữ liệu (tránh gọi lại khi chuyển tab)
        if (!viewModel.hasData() && categoryId > 0) {
            viewModel.fetchFoodsByCategory(categoryId);
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
