package com.tonyjhuang.tsunami.ui.profile.waves;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.injection.BrowseWavesModule;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by tony on 1/27/15.
 */
public class BrowseWavesActivity extends TsunamiActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    WaveMapView mapView;
    @Inject
    TsunamiPreferences preferences;

    public static void startBrowseWavesActivity(TsunamiActivity activity) {
        Intent intent = new Intent(activity, BrowseWavesActivity.class);
        activity.startActivityForResult(intent, TsunamiConstants.BROWSE_WAVES_REQUEST_CODE);
    }

    public static void startBrowseWavesActivity(TsunamiActivity activity, long userId) {
        Intent intent = new Intent(activity, BrowseWavesActivity.class);
        intent.putExtra(TsunamiConstants.USER_ID_EXTRA, userId);
        activity.startActivityForResult(intent, TsunamiConstants.BROWSE_WAVES_REQUEST_CODE);
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.asList(new BrowseWavesModule(this));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_waves);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapView.setMapFragment(mapFragment);
        mapView.setAdjustToLocationUpdate(false);
        if (savedInstanceState == null) {
            // If this is the first time we're setting up shop, set the map to the last known location
            // of the user.
            mapView.setStartingLocation(preferences.lastSeenLat.get(), preferences.lastSeenLng.get());
        }

        BrowseWavesViewPagerFragment viewPagerFragment;
        if (savedInstanceState == null) {
            long userId = getIntent().getLongExtra(TsunamiConstants.USER_ID_EXTRA, TsunamiConstants.USER_ID_EXTRA_DEFAULT);
            viewPagerFragment = BrowseWavesViewPagerFragment.getInstance(userId);
            getFragmentManager().beginTransaction()
                    .replace(R.id.view_pager_container, viewPagerFragment)
                    .commit();
        } else {
            viewPagerFragment = (BrowseWavesViewPagerFragment)
                    getFragmentManager().findFragmentById(R.id.view_pager_container);
        }
        viewPagerFragment.setOnWaveSelectedListener(mapView::displayWave);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.getBackground().setAlpha(255);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
