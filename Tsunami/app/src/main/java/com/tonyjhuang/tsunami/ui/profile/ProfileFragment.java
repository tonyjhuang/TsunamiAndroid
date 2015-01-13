package com.tonyjhuang.tsunami.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiFragment;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.ObservableParallaxScrollView;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Created by tony on 1/12/15.
 */
public class ProfileFragment extends TsunamiFragment {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.scrollview)
    ObservableParallaxScrollView scrollView;
    @InjectView(R.id.cover)
    ImageView coverImage;
    @InjectView(R.id.user_id)
    TextView userId;


    @InjectViews({R.id.num_waves,
            R.id.num_waves_views,
            R.id.num_waves_ripples,
            R.id.num_views,
            R.id.num_ripples,
            R.id.percent_rippled})
    List<ProfileStatTextSwitcher> statViews;

    @Inject
    TsunamiApi api;
    @Inject
    TsunamiPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
