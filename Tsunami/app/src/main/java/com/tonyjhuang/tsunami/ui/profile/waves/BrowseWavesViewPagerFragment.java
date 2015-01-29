package com.tonyjhuang.tsunami.ui.profile.waves;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class BrowseWavesViewPagerFragment extends TsunamiFragment {

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    @Inject
    TsunamiApi api;

    private BrowseWavesAdapter adapter;
    private OnWaveSelectedListener listener;

    public static BrowseWavesViewPagerFragment getInstance() {
        return new BrowseWavesViewPagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bw_view_pager, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribe(api.getCurrentUserWaves(), this::setAdapter);
    }

    private void setAdapter(List<Wave> waves) {
        adapter = new BrowseWavesAdapter(waves);
        viewPager.setAdapter(adapter);
        notifyListener();
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                notifyListener();
            }
        });
    }


    public void setOnWaveSelectedListener(OnWaveSelectedListener listener) {
        this.listener = listener;
        notifyListener();
    }

    private void notifyListener() {
        if (listener != null && adapter != null)
            listener.onWaveSelected(adapter.getWave(viewPager.getCurrentItem()));
    }

    public static interface OnWaveSelectedListener {
        public void onWaveSelected(Wave wave);
    }

    private class BrowseWavesAdapter extends FragmentStatePagerAdapter {
        private List<Wave> waves;

        public BrowseWavesAdapter(@NonNull List<Wave> waves) {
            super(getFragmentManager());
            this.waves = waves;
        }

        @Override
        public BrowseWavesSingleWaveFragment getItem(int position) {
            Wave wave = getWave(position);
            return BrowseWavesSingleWaveFragment.getInstance(wave.getId());
        }

        @Override
        public int getCount() {
            return waves.size();
        }

        public Wave getWave(int position) {
            return waves.get(position);
        }
    }
}
