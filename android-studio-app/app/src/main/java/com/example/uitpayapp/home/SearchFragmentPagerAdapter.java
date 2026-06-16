package com.example.uitpayapp.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SearchFragmentPagerAdapter extends FragmentStateAdapter {

    public SearchFragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new SearchStoreFragment();
        } else {
            return new SearchFoodFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
