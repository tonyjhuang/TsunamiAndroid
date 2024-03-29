package com.tonyjhuang.tsunami.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.tonyjhuang.tsunami.BuildConfig;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.injection.MainModule;
import com.tonyjhuang.tsunami.logging.TLocalytics;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.mock.DebugLocationControls;
import com.tonyjhuang.tsunami.receivers.LocationBroadcastReceiver;
import com.tonyjhuang.tsunami.ui.customviews.GhettoToolbar;
import com.tonyjhuang.tsunami.ui.customviews.fab.FloatingActionButton;
import com.tonyjhuang.tsunami.ui.drawer.NavDrawerFragment;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentScrollView;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.ui.profile.ProfileActivity;
import com.tonyjhuang.tsunami.utils.Celebration;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.tonyjhuang.tsunami.ui.drawer.NavDrawerFragment.DrawerItem;

public class MainActivity extends TsunamiActivity implements
        MainView,
        WaveContentView.OnScrollListener,
        WaveContentView.OnViewTypeChangedListener,
        NavDrawerFragment.OnDrawerItemSelectedListener {

    public final static int MIN_CELEBRATION = 2;
    public final static int MAX_CELEBRATION = 5;

    public final static int INITIAL_CELEBRATION_DELAY = 1000;
    public final static int CELEBRATION_DELAY = 250; // in millis

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.content_scrollview)
    WaveContentScrollView contentView;
    @InjectView(R.id.splash_button)
    FloatingActionButton splashButton;
    @InjectView(R.id.profile)
    Button profileButton;
    @InjectView(R.id.ghetto_toolbar)
    GhettoToolbar toolbar;
    @InjectView(R.id.profile)
    Button profile;
    @InjectView(R.id.debug_controls_stub)
    ViewStub debugControlsStub;

    DebugLocationControls debugLocationControls;

    @Inject
    WavePresenter presenter;
    @Inject
    WaveMapView mapView;
    @Inject
    TsunamiPreferences preferences;
    @Inject
    LocationInfo locationInfo;

    private static Random random = new Random();

    /**
     * keep track of the last reported location to avoid sending duplicate reports to our poor presenter.
     */
    private float lastLat, lastLng;
    private DrawerItem currentDrawerItem;

    private LocationBroadcastReceiver locationBroadcastReceiver;

    public static void startMainActivity(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.asList(new MainModule(this));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationLibrary.forceLocationUpdate(this);

        locationBroadcastReceiver = new LocationBroadcastReceiver();
        locationBroadcastReceiver.setCallbacks(locationCallback);

        Timber.d("debug: " + BuildConfig.DEBUG);

        /**
         * Pass a reference to this MainView to our presenter.
         */
        presenter.setMainView(this);

        /**
         * Create our WaveMapView that will handle manipulating and drawing on our map fragment.
         */
        presenter.setMapView(mapView);
        mapView.setMapFragment((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        if (savedInstanceState == null) {
            // If this is the first time we're setting up shop, set the map to the last known location
            // of the user.
            mapView.setStartingLocation(preferences.lastSeenLat.get(), preferences.lastSeenLng.get());
        }

        /**
         * Initialize our WaveContentView which will handle the displaying of wave info.
         */
        presenter.setContentView(contentView);
        contentView.setOnScrollListener(this);
        contentView.setOnViewTypeChangedListener(this);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        NavDrawerFragment drawerFragment =
                (NavDrawerFragment) getFragmentManager().findFragmentById(R.id.drawer);
        drawerFragment.setDrawerLayout(drawerLayout);
        drawerFragment.setOnDrawerItemSelectedListener(this);

        if (BuildConfig.DEBUG) {
            /*debugLocationControls = (DebugLocationControls) debugControlsStub.inflate();
            debugLocationControls.setLocationListener(presenter::onLocationUpdate);*/
        }
    }


    @Override
    public void onDrawerItemSelected(DrawerItem drawerItem) {
        if (drawerItem.equals(currentDrawerItem)) return;
        currentDrawerItem = drawerItem;
        switch (drawerItem) {
            case DISCOVER:
                break;
            case STATS:
                presenter.onProfileButtonClicked();
                break;
        }
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (presenter != null) presenter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        TLocalytics.tagScreen(TLocalytics.SCREEN_MAIN);
        TLocalytics.Session.startSession();
        IntentFilter locationIntentFilter = new IntentFilter();
        locationIntentFilter.addAction(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        locationIntentFilter.addAction(LocationLibraryConstants.getLocationChangedTickerBroadcastAction());
        registerReceiver(locationBroadcastReceiver, locationIntentFilter);
        LocationLibrary.forceLocationUpdate(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        TLocalytics.Session.endSession();
        preferences.lastSeenLat.set(locationInfo.lastLat);
        preferences.lastSeenLng.set(locationInfo.lastLong);
        unregisterReceiver(locationBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outParcel) {
        if (presenter != null) presenter.onSaveInstanceState(outParcel);
        super.onSaveInstanceState(outParcel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TsunamiConstants.SPLASH_REQUEST_CODE) {
            if(resultCode == TsunamiConstants.SPLASH_CREATED) {
                Wave splashed = (Wave) data.getSerializableExtra(TsunamiConstants.WAVE_RESULT_EXTRA);
                presenter.onSplash(splashed);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showCelebration() {
        int numOfCelebration = randInt(MIN_CELEBRATION, MAX_CELEBRATION);
        int delayBetweenCelebrations = CELEBRATION_DELAY;
        Handler handler = new Handler();
        for (int i = 0; i < numOfCelebration; i++) {
            handler.postDelayed(() -> Celebration.createRandomOneShot(this, container),
                    delayBetweenCelebrations * i + INITIAL_CELEBRATION_DELAY);
        }

    }

    private int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    @OnClick(R.id.splash_button)
    public void onSplashButtonClick(View view) {
        if (isUserSplashing())
            presenter.onSendSplashButtonClicked();
        else
            presenter.onBeginSplashButtonClicked();
    }

    @OnLongClick(R.id.splash_button)
    boolean onSplashButtonLongClick() {
        String msg;
        if (isUserSplashing()) {
            msg = getString(R.string.main_splash_send_description);
        } else {
            msg = getString(R.string.main_splash_begin_description);
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        return true;
    }


    @OnClick(R.id.profile)
    public void onProfileButtonClick(View view) {
        if (isUserSplashing())
            presenter.onCancelSplashButtonClicked();
        else
            presenter.onProfileButtonClicked();
    }

    @OnLongClick(R.id.profile)
    boolean onProfileButtonLongClick() {
        String msg;
        if (isUserSplashing()) {
            msg = getString(R.string.main_splash_cancel_description);
        } else {
            msg = getString(R.string.main_profile_description);
        }

        showToast(msg);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isUserSplashing())
            presenter.onCancelSplashButtonClicked();
        else
            super.onBackPressed();
    }

    private boolean isUserSplashing() {
        return contentView.getCurrentViewType().equals(WaveContentView.ViewType.SPLASHING);
    }

    @Override
    public void openProfileView() {
        ProfileActivity.startProfileActivity(this);
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(splashButton.getWindowToken(), 0);
    }

    /**
     * Our LocationBroadcastReceiver Callback handler.
     */
    private final LocationBroadcastReceiver.Callbacks locationCallback = (locationInfo) -> {
        float tmpLastLat = lastLat;
        float tmpLastLng = lastLng;

        lastLat = locationInfo.lastLat;
        lastLng = locationInfo.lastLong;

        // filter out repeated locations.
        if (locationInfo.lastLat == tmpLastLat && locationInfo.lastLong == tmpLastLng) return;

        this.locationInfo = locationInfo;
        presenter.onLocationUpdate(locationInfo);

        if (BuildConfig.DEBUG && debugLocationControls != null) {
            debugLocationControls.setCurrentLocation(locationInfo);
        }
    };


    @Override
    public void onScroll(View view, int l, int t, int oldl, int oldt) {
        int[] viewPos = new int[2];
        view.getLocationOnScreen(viewPos);
        int viewLeft = viewPos[0];
        int viewRight = viewLeft + view.getWidth();
        int viewTop = viewPos[1];

        int[] profilePos = new int[2];
        profile.getLocationOnScreen(profilePos);
        int profileLeft = profilePos[0];
        int profileRight = profileLeft + profile.getWidth();

        int[] splashPos = new int[2];
        splashButton.getLocationOnScreen(splashPos);
        int splashLeft = splashPos[0];
        //  int splashRight = splashLeft + splashButton.getWidth();

        // If the content doesnt overlap with our profile or splash button, don't do anything.
        if (profileRight < viewLeft && viewRight < splashLeft) return;

        /**
         * If our card is touching or above the lower edge of our splash button, hide it.
         */
        if (toolbar.getTop() + toolbar.getHeight() >= viewTop) {
            toolbar.hide();
        } else {
            toolbar.show();
        }
    }

    @Override
    public void onViewTypeChanged(WaveContentView.ViewType viewType) {
        switch (viewType) {
            case SPLASHING:
                splashButton.setImageResource(R.drawable.splash);
                profileButton.setBackgroundResource(R.drawable.ic_action_cancel);
                break;
            case NO_WAVES:
            case LOADING:
            case CONTENT:
                splashButton.setImageResource(R.drawable.ic_action_content_new);
                profileButton.setBackgroundResource(R.drawable.profile);
                break;
        }
    }
}
