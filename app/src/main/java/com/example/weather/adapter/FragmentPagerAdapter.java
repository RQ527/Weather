package com.example.weather.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.weather.view.HomeFragment;

import java.util.List;

/**
 * ...
 * fragment适配器
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/29
 */
public class FragmentPagerAdapter extends FragmentStateAdapter {
    private static final String TAG = "RQ";
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

    @Override
    public long getItemId(int position) {
        return fragments.get(position).hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        return false;
    }


}
