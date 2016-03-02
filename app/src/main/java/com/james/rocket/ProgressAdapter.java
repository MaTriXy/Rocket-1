package com.james.rocket;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ProgressAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"Easy", "Medium", "Hard", "Extreme"};

    public ProgressAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ProgressFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("title", titles[position] + " Mode");
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
