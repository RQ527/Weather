package com.example.weather.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.weather.view.HomeFragment;

import java.util.List;

/**
 * ...
 *
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/29
 */
public class FragmentPagerAdapter extends FragmentStateAdapter {
    private final List<HomeFragment> fragments;
    public FragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<HomeFragment> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

}
