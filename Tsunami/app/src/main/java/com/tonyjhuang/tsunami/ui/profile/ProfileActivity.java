package com.tonyjhuang.tsunami.ui.profile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonyjhuang.tsunami.BuildConfig;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiApplication;
import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.injection.ProfileModule;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.ObservableParallaxScrollView;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.OnScrollListener;
import com.tonyjhuang.tsunami.utils.ScreenManager;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnLongClick;

/**
 * Created by tony on 12/21/14.
 */
public class ProfileActivity extends TsunamiActivity implements OnScrollListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.scrollview)
    ObservableParallaxScrollView scrollView;
    @InjectView(R.id.cover)
    ImageView coverImage;
    @InjectView(R.id.user_id)
    TextView userId;

    @InjectViews({R.id.num_waves, R.id.num_waves_views, R.id.num_waves_ripples,
            R.id.num_views, R.id.num_ripples, R.id.percent_rippled})
    List<ProfileStatTextSwitcher> statViews;

    @Inject
    TsunamiApi api;
    @Inject
    TsunamiPreferences preferences;

    public static void startProfileActivity(TsunamiActivity activity) {
        activity.startActivityForResult(
                new Intent(activity, ProfileActivity.class), TsunamiConstants.PROFILE_REQUEST_CODE);
    }

    @Override
    protected List<Object> getMyModules() {
        return Arrays.asList(new ProfileModule());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        subscribe(api.getCurrentUserStats(), this::populateStats, (throwable) -> {
            Timber.e(throwable, "error getting userstats");
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setToolbarBackgroundAlpha(0);

        scrollView.setOnScrollListener(this);

        int orientation = getResources().getConfiguration().orientation;
        // If portrait, coverImage takes 1/3 of screen height, else 1/2
        float screenModifier = orientation == Configuration.ORIENTATION_PORTRAIT ? 3.0f : 2.0f;
        setCoverImageHeight((int) (ScreenManager.getScreenDimens(this).y / screenModifier));

        Picasso.with(this).load(TsunamiApplication.profileCoverResourceId).into(coverImage);

        if (BuildConfig.DEBUG) {
            userId.setVisibility(View.VISIBLE);
            userId.setText(preferences.id.get());
        }
    }

    Handler handler = new Handler();

    private void populateStats(UserStats stats) {
        handler.post(() -> {
            List<String> statStrings = statsToList(stats);
            for (int i = 0; i < statViews.size(); i++) {
                TextSwitcher view = statViews.get(i);
                String stat = statStrings.get(i);
                handler.postDelayed(() -> {
                    view.post(() -> view.setText(stat));
                }, 150 * i);
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

    /**
     * @param alpha 0 is transparent, 255 is opaque
     */
    private void setToolbarBackgroundAlpha(int alpha) {
        toolbar.getBackground().setAlpha(alpha);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        int coverImageHeight = coverImage.getHeight();
        int ratio = (int) (((float) Math.min(Math.max(t, 0), coverImageHeight)) / coverImageHeight * 255);
        setToolbarBackgroundAlpha(ratio);
    }

    private void setCoverImageHeight(int height) {
        coverImage.post(() -> {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coverImage.getLayoutParams();
            layoutParams.height = height;
            coverImage.setLayoutParams(layoutParams);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnLongClick(R.id.user_id)
    public boolean onUserIdLongClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("user id", userId.getText());
        clipboard.setPrimaryClip(clip);
        showToast("User id copied to clipboard");
        return true;
    }
}
