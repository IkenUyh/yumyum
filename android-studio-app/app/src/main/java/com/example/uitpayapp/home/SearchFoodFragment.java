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

    private void setupSearchResults() {
        allFoods = new ArrayList<>();
        allFoods.addAll(HomeActivity.HomeRepository.getInstance().getDealFoods());
        allFoods.addAll(HomeActivity.HomeRepository.getInstance().getPopularFoods());
        
        filteredFoods = new ArrayList<>(allFoods);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchFoodAdapter(filteredFoods, (item, sourceImage) -> {
            showFoodItemDetailPopup(item, sourceImage);
        });
        rvSearchResults.setAdapter(searchAdapter);
    }

    private void filter(String query) {
        if (query.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);

            filteredFoods.clear();
            for (FoodMenuItem f : allFoods) {
                if (f.getName().toLowerCase().contains(query)) {
                    filteredFoods.add(f);
                }
            }
            searchAdapter.notifyDataSetChanged();
        }
    }

    private void showFoodItemDetailPopup(FoodMenuItem item, ImageView sourceImage) {
        if (getContext() == null) return;
        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(getContext(), item, null, (selectedItem, quantity, selectedToppings) -> {
            CartItem newItem = new CartItem(selectedItem, quantity, selectedToppings);
            CartManager.getInstance().addItemSync(newItem, new com.example.uitpayapp.network.ApiCallback<String>() {
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
            // item_food_recommend_deal has tv_restaurant_name (used as food name), tv_price
            // Let's adapt
            if(holder.tvName != null) holder.tvName.setText(item.getName());
            if(holder.tvPrice != null) holder.tvPrice.setText(item.getFormattedPrice());
            if(holder.ivImage != null) holder.ivImage.setImageResource(item.getImageResId());
            
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item, holder.ivImage));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice;
            ImageView ivImage;
            public ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_food_title);
                tvPrice = itemView.findViewById(R.id.tv_discount_price);
                ivImage = itemView.findViewById(R.id.iv_food_image);
            }
        }
    }
}
