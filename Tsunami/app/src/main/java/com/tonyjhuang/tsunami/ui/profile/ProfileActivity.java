package com.tonyjhuang.tsunami.ui.profile;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;

import com.squareup.picasso.Picasso;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.TsunamiApplication;
import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.injection.ProfileModule;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.ObservableParallaxScrollView;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.OnScrollListener;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.InjectViews;

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


    @InjectViews({R.id.num_waves, R.id.num_waves_views, R.id.num_waves_ripples,
            R.id.num_views, R.id.num_ripples, R.id.percent_rippled})
    List<ProfileStatTextSwitcher> statViews;

    @Inject
    TsunamiApi api;

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
            showToast("error getting user stats");
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
        setCoverImageHeight((int) (getScreenDimensions().y / screenModifier));

        Picasso.with(this).load(TsunamiApplication.profileCoverResourceId).into(coverImage);
    }

    Handler handler = new Handler();
    boolean set = false;
    private void populateStats(UserStats stats) {
        this.handler.post(() -> {
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

    private Point getScreenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
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
}
