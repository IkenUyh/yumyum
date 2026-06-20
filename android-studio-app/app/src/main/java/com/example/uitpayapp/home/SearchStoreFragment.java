package com.example.uitpayapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.Restaurant;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchStoreFragment extends Fragment {

    private View layoutEmptyState;
    private ChipGroup cgPopularSearches;
    private RecyclerView rvSearchResults;

    private SearchStoreAdapter searchAdapter;

    private List<Restaurant> allStores;
    private List<Restaurant> filteredStores;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
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
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        filter(s.toString().trim().toLowerCase());
                    }
                });

                // Initial check
                filter(etSearchInput.getText().toString().trim().toLowerCase());
            }
        }

        return view;
    }

    private void setupPopularSearches() {
        List<String> keywords = Arrays.asList("KFC", "Phúc Long", "Highlands", "Gà rán", "Trà sữa", "Cơm tấm");

        cgPopularSearches.removeAllViews();
        for (String keyword : keywords) {
            Chip chip = new Chip(getContext());
            chip.setText(keyword);
            chip.setChipBackgroundColorResource(R.color.white); // Assuming white, you can customize
            chip.setTextColor(0xFF333333);
            chip.setTextSize(14);
            // Optionally, add a slight border or use default Chip style

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
        filteredStores = new ArrayList<>();

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchStoreAdapter(filteredStores, restaurant -> {
            Intent intent = new Intent(getContext(), StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
            if (restaurant.getId() != null) {
                intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_ID, restaurant.getId());
            }
            startActivity(intent);
        });
        rvSearchResults.setAdapter(searchAdapter);
    }

    private void filter(String query) {
        currentQuery = query;
        if (query.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            filteredStores.clear();
            searchAdapter.notifyDataSetChanged();
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);

            new com.example.uitpayapp.modules.restaurant.RestaurantRepository().searchByKeyword(query, new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.restaurant.models.RestaurantDistanceViewDTO>>() {
                @Override
                public void onSuccess(List<com.example.uitpayapp.modules.restaurant.models.RestaurantDistanceViewDTO> result) {
                    if (!query.equals(currentQuery)) {
                        return;
                    }
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        filteredStores.clear();
                        if (result != null) {
                            for (com.example.uitpayapp.modules.restaurant.models.RestaurantDistanceViewDTO dto : result) {
                                Restaurant r = new Restaurant(
                                        dto.getId(),
                                        dto.getName(),
                                        dto.getName(),
                                        0,
                                        "", // category stub
                                        new ArrayList<>(),
                                        0,
                                        dto.getRatingAverage() != null ? dto.getRatingAverage() : 0.0,
                                        dto.getReviewCount() != null ? dto.getReviewCount() : 0,
                                        0,
                                        dto.getAddress(),
                                        dto.getImageUrl()
                                );
                                r.setDistance(dto.getDistance());
                                filteredStores.add(r);
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
                        filteredStores.clear();
                        searchAdapter.notifyDataSetChanged();
                    });
                }
            });
        }
    }

    public static class SearchStoreAdapter extends RecyclerView.Adapter<SearchStoreAdapter.ViewHolder> {
        private final List<Restaurant> items;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(Restaurant item);
        }

        public SearchStoreAdapter(List<Restaurant> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_vertical, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Restaurant item = items.get(position);
            holder.tvName.setText(item.getName());
            
            // Set category or default placeholder
            if (item.getCategory() != null && !item.getCategory().isEmpty()) {
                holder.tvCategory.setText(item.getCategory());
            } else {
                holder.tvCategory.setText("Quán ăn");
            }

            // Bind rating and review count
            holder.tvRating.setText(String.format(java.util.Locale.US, "%.1f", item.getRating()));
            holder.tvReviewCount.setText("(" + item.getReviewCount() + ")");

            // Bind distance if available
            if (item.getDistance() != null) {
                holder.tvDistance.setText(String.format(java.util.Locale.US, "%.1f km", item.getDistance()));
                holder.tvDistance.setVisibility(View.VISIBLE);
            } else {
                holder.tvDistance.setVisibility(View.GONE);
            }

            // Load remote image with placeholder animation
            com.example.uitpayapp.utils.ImageLoadHelper.loadImageWithFlashingPlaceholder(holder.ivStoreImage, item.getImageUrl());

            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.ImageView ivStoreImage;
            TextView tvName;
            TextView tvCategory;
            TextView tvRating;
            TextView tvReviewCount;
            TextView tvDistance;

            public ViewHolder(View itemView) {
                super(itemView);
                ivStoreImage = itemView.findViewById(R.id.iv_store_image);
                tvName = itemView.findViewById(R.id.tv_store_name);
                tvCategory = itemView.findViewById(R.id.tv_store_category);
                tvRating = itemView.findViewById(R.id.tv_store_rating);
                tvReviewCount = itemView.findViewById(R.id.tv_review_count);
                tvDistance = itemView.findViewById(R.id.tv_store_distance);
            }
        }
    }
}
