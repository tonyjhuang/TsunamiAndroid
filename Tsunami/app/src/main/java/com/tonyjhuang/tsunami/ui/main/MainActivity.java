package com.tonyjhuang.tsunami.ui.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.injection.MainModule;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.GhettoToolbar;
import com.tonyjhuang.tsunami.ui.customviews.fab.FloatingActionButton;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentScrollView;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.ui.profile.ProfileActivity;
import com.tonyjhuang.tsunami.utils.Celebration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;


public class MainActivity extends TsunamiActivity implements
        MainView,
        WaveContentView.OnScrollListener,
        WaveContentView.OnViewTypeChangedListener {

    public final static int MIN_CELEBRATION = 2;
    public final static int MAX_CELEBRATION = 5;

    public final static int INITIAL_CELEBRATION_DELAY = 1000;
    public final static int CELEBRATION_DELAY = 250; // in millis

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

    @Inject
    WavePresenter presenter;
    @Inject
    WaveMapView mapView;

    private static Random random = new Random();

    private final String STATE_WAVE = "wave";
    private final String STATE_SPLASHING = "splashing";

    public static void startMainActivity(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected List<Object> getMyModules() {
        return Arrays.asList(new MainModule(this));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationLibrary.forceLocationUpdate(this);

        /**
         * Pass a reference to this MainView to our presenter.
         */
        presenter.setMainView(this);

        /**
         * Create our WaveMapView that will handle manipulating and drawing on our map fragment.
         */
        presenter.setMapView(mapView);
        mapView.setMapFragment((MapFragment) getFragmentManager().findFragmentById(R.id.map));

        /**
         * Initialize our WaveContentView which will handle the displaying of wave info.
         */
        presenter.setContentView(contentView);
        contentView.setOnScrollListener(this);
        contentView.setOnViewTypeChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("registering broadcast receiver");
        IntentFilter locationIntentFilter = new IntentFilter();
        locationIntentFilter.addAction(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        locationIntentFilter.addAction(LocationLibraryConstants.getLocationChangedTickerBroadcastAction());
        registerReceiver(mainLocationBroadcastReceiver, locationIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("unregistering broadcast receiver");
        unregisterReceiver(mainLocationBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putParcelable(STATE_WAVE, contentView.getContentWave());
        //savedInstanceState.putBoolean(STATE_SPLASHING, contentView.isShowingSplashCard());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
        if (contentView.isShowingSplashCard())
            presenter.onSendSplashButtonClicked();
        else
            presenter.onBeginSplashButtonClicked();
    }

    @OnLongClick(R.id.splash_button)
    boolean onSplashButtonLongClick() {
        String msg;
        if (contentView.isShowingSplashCard()) {
            msg = getString(R.string.main_splash_send_description);
        } else {
            msg = getString(R.string.main_splash_begin_description);
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        return true;
    }


    @OnClick(R.id.profile)
    public void onProfileButtonClick(View view) {
        if (contentView.isShowingSplashCard())
            presenter.onCancelSplashButtonClicked();
        else
            presenter.onProfileButtonClicked();
    }

    @OnLongClick(R.id.profile)
    boolean onProfileButtonLongClick() {
        String msg;
        if (contentView.isShowingSplashCard()) {
            msg = getString(R.string.main_splash_cancel_description);
        } else {
            msg = getString(R.string.main_profile_description);
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (contentView.isShowingSplashCard())
            presenter.onCancelSplashButtonClicked();
        else
            super.onBackPressed();
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
     * Our little broadcast receiver that will listen for location updates.
     */
    private final BroadcastReceiver mainLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // extract the location info in the broadcast
            presenter.onLocationUpdate((LocationInfo) intent
                    .getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO));
        }
    };

    @Override
    public void onScroll(View view, int l, int t, int oldl, int oldt) {
        /**
         * If our card is touching or above the lower edge of our splash button, hide it.
         */
        if (t >= view.getHeight() - (toolbar.getTop() + toolbar.getHeight())) {
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
            case CONTENT:
                splashButton.setImageResource(R.drawable.ic_action_content_new);
                profileButton.setBackgroundResource(R.drawable.profile);
                break;
        }
    }
}
