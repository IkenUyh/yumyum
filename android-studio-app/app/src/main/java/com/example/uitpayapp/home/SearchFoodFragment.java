package com.example.uitpayapp.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.uitpayapp.utils.CartAnimationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFoodFragment extends Fragment {

    private View layoutEmptyState;
    private ChipGroup cgPopularSearches;
    private RecyclerView rvSearchResults;

    private SearchFoodAdapter searchAdapter;
    
    private List<FoodMenuItem> allFoods;
    private List<FoodMenuItem> filteredFoods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_tab, container, false);

        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        cgPopularSearches = view.findViewById(R.id.cg_popular_searches);
        rvSearchResults = view.findViewById(R.id.rv_search_results);

        setupPopularSearches();
        setupSearchResults();

        if (getActivity() instanceof SearchActivity) {
            EditText etSearchInput = ((SearchActivity) getActivity()).getSearchInput();
            if (etSearchInput != null) {
                etSearchInput.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        filter(s.toString().trim().toLowerCase());
                    }
                });
                
                filter(etSearchInput.getText().toString().trim().toLowerCase());
            }
        }

        return view;
    }

    private void setupPopularSearches() {
        List<String> keywords = Arrays.asList("Gà rán KFC", "Trà sữa Thái", "Cà phê đen", "Pizza xúc xích", "Bánh mì chà bông");
        
        cgPopularSearches.removeAllViews();
        for (String keyword : keywords) {
            Chip chip = new Chip(getContext());
            chip.setText(keyword);
            chip.setChipBackgroundColorResource(R.color.white);
            chip.setTextColor(0xFF333333);
            chip.setTextSize(14);
            
            chip.setOnClickListener(v -> {
                if (getActivity() instanceof SearchActivity) {
                    EditText etSearchInput = ((SearchActivity) getActivity()).getSearchInput();
                    if (etSearchInput != null) {
                        etSearchInput.setText(keyword);
                        etSearchInput.setSelection(keyword.length());
                    }
                }
            });
            cgPopularSearches.addView(chip);
        }
    }

    private String currentQuery = "";

    private void setupSearchResults() {
        filteredFoods = new ArrayList<>();

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchFoodAdapter(filteredFoods, (item, sourceImage) -> {
            showFoodItemDetailPopup(item, sourceImage);
        });
        rvSearchResults.setAdapter(searchAdapter);
    }

    private void filter(String query) {
        currentQuery = query;
        if (query.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            filteredFoods.clear();
            searchAdapter.notifyDataSetChanged();
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);

            com.example.uitpayapp.network.SessionManager sessionManager = com.example.uitpayapp.network.SessionManager.getInstance(getContext());
            Double lat = null;
            Double lng = null;
            if (sessionManager.getDeliveryLatitude() != 0.0 || sessionManager.getDeliveryLongitude() != 0.0) {
                lat = sessionManager.getDeliveryLatitude();
                lng = sessionManager.getDeliveryLongitude();
            }

            new com.example.uitpayapp.modules.food.FoodRepository().searchFoodsByKeyword(query, lat, lng, new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.food.models.responses.FoodResponse>>() {
                @Override
                public void onSuccess(List<com.example.uitpayapp.modules.food.models.responses.FoodResponse> result) {
                    if (!query.equals(currentQuery)) {
                        return;
                    }
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        filteredFoods.clear();
                        if (result != null) {
                            for (com.example.uitpayapp.modules.food.models.responses.FoodResponse res : result) {
                                FoodMenuItem item = new FoodMenuItem(
                                        res.getId().toString(),
                                        res.getName(),
                                        res.getPrice() != null ? res.getPrice().longValue() : 0L,
                                        0,
                                        res.getDescription(),
                                        res.getImageUrl()
                                );
                                item.setRestaurantId(res.getRestaurantId());
                                item.setRestaurantName(res.getRestaurantName());
                                item.setDistance(res.getDistance());
                                filteredFoods.add(item);
                            }
                        }
                        searchAdapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    if (!query.equals(currentQuery)) {
                        return;
                    }
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        filteredFoods.clear();
                        searchAdapter.notifyDataSetChanged();
                    });
                }
            });
        }
    }

    private void showFoodItemDetailPopup(FoodMenuItem item, ImageView sourceImage) {
        if (getContext() == null) return;
        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(getContext(), item, null, (selectedItem, quantity, selectedToppings) -> {
            CartItem newItem = new CartItem(selectedItem, quantity, selectedToppings);
            CartManager.getInstance().addItemSync(getContext(), newItem, new com.example.uitpayapp.network.ApiCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            View btnCart = getActivity().findViewById(R.id.btn_cart);
                            CartAnimationHelper.animateFlyToCart(getActivity(), sourceImage != null ? sourceImage : getActivity().findViewById(android.R.id.content), btnCart, () -> {
                                if (getActivity() instanceof SearchActivity) {
                                    ((SearchActivity) getActivity()).updateGlobalCartBadge();
                                }
                            });
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            android.widget.Toast.makeText(getContext(), "Không thể thêm vào giỏ hàng: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        });
    }

    // Custom adapter for search results
    public static class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodAdapter.ViewHolder> {
        private final List<FoodMenuItem> items;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(FoodMenuItem item, ImageView sourceImage);
        }

        public SearchFoodAdapter(List<FoodMenuItem> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Reusing topic store item layout for simplicity, it works as a horizontal card but we can wrap it
            // Or better, let's create a simple linear layout dynamically or reuse item_food_recommend_deal
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food_recommend_deal, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FoodMenuItem item = items.get(position);
            if(holder.tvName != null) holder.tvName.setText(item.getName());
            if(holder.tvPrice != null) holder.tvPrice.setText(item.getFormattedPrice());
            if(holder.ivImage != null) {
                com.example.uitpayapp.utils.ImageLoadHelper.loadImageWithFlashingPlaceholder(holder.ivImage, item.getImageUrl());
            }
            if(holder.tvStoreName != null) {
                if (item.getRestaurantName() != null && !item.getRestaurantName().isEmpty()) {
                    holder.tvStoreName.setText(item.getRestaurantName());
                } else {
                    holder.tvStoreName.setText("Cửa hàng");
                }
            }

            if(holder.tvDistance != null) {
                if(item.getDistance() != null) {
                    holder.tvDistance.setVisibility(View.VISIBLE);
                    holder.tvDistance.setText(String.format(java.util.Locale.US, "%.1f km", item.getDistance()));
                } else {
                    holder.tvDistance.setVisibility(View.GONE);
                }
            }
            
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item, holder.ivImage));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice, tvStoreName, tvDistance;
            ImageView ivImage;
            public ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_food_title);
                tvPrice = itemView.findViewById(R.id.tv_discount_price);
                ivImage = itemView.findViewById(R.id.iv_food_image);
                tvStoreName = itemView.findViewById(R.id.tv_store_name);
                tvDistance = itemView.findViewById(R.id.tv_distance);
                
                // Hide unnecessary elements for search view
                View discountTag = itemView.findViewById(R.id.tv_discount_tag);
                if (discountTag != null) discountTag.setVisibility(View.GONE);

                View soldInfo = itemView.findViewById(R.id.ll_sold_info);
                if (soldInfo != null) soldInfo.setVisibility(View.GONE);

                View originalPrice = itemView.findViewById(R.id.tv_original_price);
                if (originalPrice != null) originalPrice.setVisibility(View.GONE);

                View btnBuyNow = itemView.findViewById(R.id.btn_buy_now);
                if (btnBuyNow != null) btnBuyNow.setVisibility(View.GONE);
            }
        }
    }
}
