package com.tonyjhuang.tsunami.ui.profile.waves;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import butterknife.InjectView;

public class BrowseWavesViewPagerFragment extends TsunamiFragment {

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    public static BrowseWavesViewPagerFragment getInstance() {
        return new BrowseWavesViewPagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browse_waves_view_pager, container, false);
    }


}
