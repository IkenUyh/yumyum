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

    private void setupSearchResults() {
        allStores = HomeActivity.HomeRepository.getInstance().getRestaurants();
        filteredStores = new ArrayList<>(allStores);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchStoreAdapter(filteredStores, restaurant -> {
            Intent intent = new Intent(getContext(), StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
            startActivity(intent);
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

            filteredStores.clear();
            for (Restaurant r : allStores) {
                if (r.getName().toLowerCase().contains(query)) {
                    filteredStores.add(r);
                }
            }
            searchAdapter.notifyDataSetChanged();
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
            holder.tvCategory.setText(item.getCategory());

            // Just map some dummy rating and distance for visual
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            TextView tvCategory;

            public ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_store_name);
                tvCategory = itemView.findViewById(R.id.tv_store_category);
            }
        }
    }
}
