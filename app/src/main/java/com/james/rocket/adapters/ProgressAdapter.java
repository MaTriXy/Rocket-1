package com.james.rocket.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.james.rocket.fragments.ProgressFragment;
import com.james.rocket.utils.PreferenceUtils;

public class ProgressAdapter extends FragmentPagerAdapter {

    public ProgressAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ProgressFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("level", getPageLevel(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    private PreferenceUtils.LevelIdentifier getPageLevel(int position) {
        switch (position) {
            case 0:
                return PreferenceUtils.LevelIdentifier.EASY;
            case 1:
                return PreferenceUtils.LevelIdentifier.MEDIUM;
            case 2:
                return PreferenceUtils.LevelIdentifier.HARD;
            case 3:
                return PreferenceUtils.LevelIdentifier.EXTREME;
            default:
                return PreferenceUtils.LevelIdentifier.EASY;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getPageLevel(position).toString();
    }
}
