package com.ixzmedia.colorgame.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ixzmedia.colorgame.fragments.EasyGameLeaderBoardFragment;
import com.ixzmedia.colorgame.fragments.HardGameLeaderBoardFragment;
import com.ixzmedia.colorgame.fragments.MediumGameLeaderBoardFragment;

public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabsAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm);
        this.mNumOfTabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                EasyGameLeaderBoardFragment easyGameLeaderBoardFragment = new EasyGameLeaderBoardFragment();
                return easyGameLeaderBoardFragment;
            case 1:
                MediumGameLeaderBoardFragment mediumGameLeaderBoardFragment = new MediumGameLeaderBoardFragment();
                return mediumGameLeaderBoardFragment;
            case 2:
                HardGameLeaderBoardFragment hardGameLeaderBoardFragment = new HardGameLeaderBoardFragment();
                return hardGameLeaderBoardFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
