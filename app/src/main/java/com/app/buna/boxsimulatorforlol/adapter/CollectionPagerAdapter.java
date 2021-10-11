package com.app.buna.boxsimulatorforlol.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.buna.boxsimulatorforlol.fragment.ChampCollectFragment;
import com.app.buna.boxsimulatorforlol.fragment.SkinCollectFragment;

public class CollectionPagerAdapter extends FragmentStatePagerAdapter {
    int numOfTab;

    public CollectionPagerAdapter(@NonNull FragmentManager fm, int numOfTab) {
        super(fm, numOfTab);
        this.numOfTab = numOfTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChampCollectFragment champCollectFragment = new ChampCollectFragment();
                return champCollectFragment;
            case 1:
                SkinCollectFragment skinCollectFrag = new SkinCollectFragment();
                return skinCollectFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTab;
    }
}
