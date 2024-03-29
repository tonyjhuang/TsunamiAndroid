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
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.util.Collections;
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

    public static BrowseWavesViewPagerFragment getInstance(long userId) {
        Bundle args = new Bundle();
        args.putLong(TsunamiConstants.USER_ID_EXTRA, userId);

        BrowseWavesViewPagerFragment fragment = new BrowseWavesViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bw_view_pager, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        long userId = getArguments().getLong(TsunamiConstants.USER_ID_EXTRA, TsunamiConstants.USER_ID_EXTRA_DEFAULT);
        if (userId == TsunamiConstants.USER_ID_EXTRA_DEFAULT) {
            subscribe(api.getCurrentUserWaves(), this::setAdapter);
        } else {
            subscribe(api.getUserWaves(userId), this::setAdapter);
        }
    }

    private void setAdapter(List<Wave> waves) {
        // Sort by created at (new waves in front).
        Collections.sort(waves, (w1, w2) -> w2.getCreatedAt().compareTo(w1.getCreatedAt()));
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
            if (waves.size() == 0) return null;
            return waves.get(position);
        }
    }
}
