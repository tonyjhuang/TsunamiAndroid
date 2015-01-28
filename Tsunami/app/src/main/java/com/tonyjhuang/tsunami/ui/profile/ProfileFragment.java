package com.tonyjhuang.tsunami.ui.profile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonyjhuang.tsunami.BuildConfig;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.ObservableParallaxScrollView;
import com.tonyjhuang.tsunami.ui.profile.waves.BrowseWavesActivity;
import com.tonyjhuang.tsunami.utils.ScreenManager;
import com.tonyjhuang.tsunami.utils.TsunamiApplication;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by tony on 1/12/15.
 */
public class ProfileFragment extends TsunamiFragment {
    private static final String USER_ID = "user_id";

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

    private Handler handler = new Handler();

    public static ProfileFragment getInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;
        // If portrait, coverImage takes 1/3 of screen height, else 1/2
        float screenModifier = orientation == Configuration.ORIENTATION_PORTRAIT ? 3.0f : 2.0f;
        setCoverImageHeight((int) (ScreenManager.getScreenDimens(getActivity()).y / screenModifier));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (BuildConfig.DEBUG) {
            userId.setVisibility(View.VISIBLE);
            userId.setText(preferences.guid.get());
        }

        Picasso.with(getActivity()).load(TsunamiApplication.profileCoverResourceId).into(coverImage);

        // Retrieve userstats from api
        subscribe(api.getCurrentUserStats(), this::populateStats,
                (throwable) -> Timber.e(throwable, "error getting userstats"));
    }

    private void setCoverImageHeight(int height) {
        coverImage.post(() -> {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coverImage.getLayoutParams();
            layoutParams.height = height;
            coverImage.setLayoutParams(layoutParams);
        });
    }

    @OnLongClick(R.id.user_id)
    public boolean onUserIdLongClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("user id", ((TextView) view).getText());
        clipboard.setPrimaryClip(clip);
        showToast("User id copied to clipboard");
        return true;
    }

    private void populateStats(UserStats stats) {
        handler.post(() -> {
            List<String> statStrings = statsToList(stats);
            for (int i = 0; i < statViews.size(); i++) {
                TextSwitcher view = statViews.get(i);
                String stat = statStrings.get(i);
                handler.postDelayed(() -> view.post(() -> view.setText(stat)), 150 * i);
            }
        });
    }

    private List<String> statsToList(UserStats stats) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(stats.getSplashes() + "");
        strings.add(stats.getViewsAcrossWaves() + "");
        strings.add(stats.getRipplesAcrossWaves() + "");
        strings.add(stats.getViewed() + "");
        strings.add(stats.getRipples() + "");
        strings.add(stats.getRippleChance() + "%");
        return strings;
    }

    @OnClick(R.id.waves)
    public void onWavesClick(View view) {
        BrowseWavesActivity.startBrowseWavesActivity(getTsunamiActivity());
    }
}
