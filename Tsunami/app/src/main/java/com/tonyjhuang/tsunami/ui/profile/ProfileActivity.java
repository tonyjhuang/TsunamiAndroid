package com.tonyjhuang.tsunami.ui.profile;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.injection.ProfileModule;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.ObservableScrollView;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.OnScrollListener;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by tony on 12/21/14.
 */
public class ProfileActivity extends TsunamiActivity implements OnScrollListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.scrollview)
    ObservableScrollView scrollView;
    @InjectView(R.id.cover)
    ImageView coverImage;

    @InjectView(R.id.num_waves)
    TextView numWaves;
    @InjectView(R.id.num_waves_views)
    TextView numWavesViews;
    @InjectView(R.id.num_waves_ripples)
    TextView numWavesRipples;

    @InjectView(R.id.num_ripples)
    TextView numRipples;

    @Inject
    TsunamiApi api;

    private static int coverPhotoIndex;
    static {
        coverPhotoIndex = new Random().nextInt(4);
    }

    public static void startProfileActivity(TsunamiActivity activity) {
        activity.startActivityForResult(
                new Intent(activity, ProfileActivity.class), TsunamiConstants.PROFILE_REQUEST_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setToolbarBackgroundAlpha(0);

        scrollView.setOnScrollListener(this);

        int orientation = getResources().getConfiguration().orientation;
        float screenModifier = orientation == Configuration.ORIENTATION_PORTRAIT ? 3.0f : 2.0f;
        setCoverImageHeight((int) (getScreenDimensions().y / screenModifier));

        int resourceId = getResources().getIdentifier("cover_" + (coverPhotoIndex + 1), "drawable", getPackageName());
        coverImage.setImageResource(resourceId);

        subscribe(api.getCurrentUserStats(), (userStats) -> {
            numWaves.setText(userStats.getSplashes() + "");
            numWavesViews.setText(userStats.getViewsAcrossWaves() + "");
            numWavesRipples.setText(userStats.getRipplesAcrossWaves() + "");
            numRipples.setText(userStats.getRipples() + "");
        });
    }

    @Override
    protected List<Object> getMyModules() {
        return Arrays.asList(new ProfileModule());
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
        int ratio = (int) (((float) Math.min(t, coverImageHeight)) / coverImageHeight * 255);
        setToolbarBackgroundAlpha(ratio);
    }

    private Point getScreenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private void setCoverImageHeight(int height) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coverImage.getLayoutParams();
        layoutParams.height = height;
        coverImage.setLayoutParams(layoutParams);
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
