package com.example.uitpayapp.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    private final List<String> categories;

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CategoryTabFragment.newInstance(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
