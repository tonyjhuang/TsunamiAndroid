package com.tonyjhuang.tsunami.ui.profile.waves;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.content.ContentCard;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by tony on 1/27/15.
 */
public class BrowseWavesSingleWaveFragment extends TsunamiFragment {
    private static final String WAVE_ID = "wave_id";

    @InjectView(R.id.container)
    FrameLayout container;

    @Inject
    TsunamiApi api;

    public static BrowseWavesSingleWaveFragment getInstance(long waveId) {
        Bundle args = new Bundle();
        args.putLong(WAVE_ID, waveId);
        BrowseWavesSingleWaveFragment fragment = new BrowseWavesSingleWaveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bw_single_wave, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        long waveId = getArguments().getLong(WAVE_ID, -1);
        subscribe(api.getWave(waveId), this::setWave);
    }

    private void setWave(Wave wave) {
        ContentCard contentCard = new ContentCard(getActivity());
        container.addView(contentCard);
        contentCard.setWave(wave);
    }
}
