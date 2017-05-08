package com.james.rocket.fragments;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    private boolean isSelected;

    public abstract String getTitle();

    public void onScrolled(float positionOffset, int positionOffsetPixels) {
    }

    public void onSelected() {
        isSelected = true;
    }

    public void onDeselected() {
        isSelected = false;
    }

    public final boolean isSelected() {
        return isSelected;
    }

}
