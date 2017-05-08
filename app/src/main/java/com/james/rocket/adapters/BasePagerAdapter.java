package com.james.rocket.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.james.rocket.fragments.BaseFragment;

public class BasePagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

    private BaseFragment[] fragments;

    public BasePagerAdapter(FragmentManager fm, BaseFragment... fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        fragments[position].onScrolled(positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < getCount(); i++) {
            if (i == position) fragments[i].onSelected();
            else if (fragments[i].isSelected()) fragments[i].onDeselected();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
