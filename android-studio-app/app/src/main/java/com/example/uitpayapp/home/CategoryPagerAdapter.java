package com.example.uitpayapp.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    private final String categoryName;
    private final List<String> filters;

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, String categoryName, List<String> filters) {
        super(fragmentActivity);
        this.categoryName = categoryName;
        this.filters = filters;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CategoryTabFragment.newInstance(categoryName, filters.get(position));
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }
}
