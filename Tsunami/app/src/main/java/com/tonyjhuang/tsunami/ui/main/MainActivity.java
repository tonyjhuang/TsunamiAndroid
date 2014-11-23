package com.tonyjhuang.tsunami.ui.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.MapFragment;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.tonyjhuang.tsunami.BuildConfig;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.button.SplashButton;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.SplashCard;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.WaveContentScrollView;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WaveMapView;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WaveMapViewImpl;

import java.util.Random;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends TsunamiActivity implements WavePresenter {

    @InjectView(R.id.content_scrollview)
    WaveContentScrollView contentView;
    @InjectView(R.id.splash_button)
    SplashButton splashButton;

    @Inject
    LocationInfo locationInfo;

    private final String STATE_WAVE = "wave";
    private final String STATE_SPLASHING = "splashing";

    /**
     * Wave that we're hiding while the user is in the process of splashing a new Wave.
     */
    private Wave cachedDuringSplash;

    /**
     * Our lovely map view that will handle drawing on the MapFragment
     */
    private WaveMapView waveMapView;

    public static void startMainActivity(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        LocationLibrary.forceLocationUpdate(this);

        /**
         * Create our WaveMapView that will handle manipulating and drawing on our map fragment.
         */
        waveMapView = new WaveMapViewImpl(this);
        waveMapView.setPresenter(this);
        waveMapView.setMapFragment((MapFragment) getFragmentManager().findFragmentById(R.id.map));

        /**
         * Initialize our WaveContentView which will handle the displaying of wave info.
         */
        contentView.setPresenter(this);
        contentView.attachSplashButton(splashButton);

        if(savedInstanceState == null) {
            /**
             * Show the user a new wave.
             */
            displayNewWave();
        } else {
            //if(savedInstanceState.getBoolean(STATE_SPLASHING)) {

//            } else {
                Wave savedStateWave = savedInstanceState.getParcelable(STATE_WAVE);
                displayWave(savedStateWave);
  //          }*/
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mainLocationBroadcastReceiver,
                new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction()));
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("unregistering.");
        unregisterReceiver(mainLocationBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_log_out) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(STATE_WAVE, contentView.getContentWave());
        savedInstanceState.putBoolean(STATE_SPLASHING, contentView.isShowingSplashCard());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Random string generators for making debugging waves. Creates titles and body text.
     */
    RandomString randomTitleGen = new RandomString(16);
    RandomString randomTextGen = new RandomString(128);

    private void displayNewWave() {
        Wave randomWave = Wave.createDebugWave(randomTitleGen.nextString(), randomTextGen.nextString());
        displayWave(randomWave);
    }

    private void displayWave(Wave wave) {
        contentView.showContentCard(wave);
        waveMapView.displayWave(wave);
    }

    @Override
    public void onContentSwipedUp() {
        Timber.d("onContentSwipedUp");
        displayNewWave();
    }

    @Override
    public void onContentSwipedDown() {
        Timber.d("onContentSwipedDown");
        displayNewWave();
    }

    @Override
    public void onSplashSwipedUp() {
        Timber.d("onSplashSwipedUp");
        contentView.showContentCard(cachedDuringSplash);
        SplashCard.SplashContent splashContent = contentView.retrieveSplashContent();
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        contentView.showContentCard(cachedDuringSplash);
    }

    @Override
    public void onSplashButtonClicked() {
        Timber.d("onSplashButtonClicked");
        if (contentView.isShowingContentCard()) {
            cachedDuringSplash = contentView.getContentWave();
            contentView.clearSplashCard();
            contentView.showSplashCard();
            waveMapView.displaySplashing();
        } else {
            contentView.showContentCard(cachedDuringSplash);
            waveMapView.displayWave(cachedDuringSplash);
        }
    }

    @OnClick(R.id.splash_button)
    public void onSplashButtonClick(View view) {
        onSplashButtonClicked();
    }

    public static class RandomString {

        private static final char[] symbols;

        static {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();

        private final char[] buf;

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }

    /**
     * Our little broadcast receiver that will listen for location updates.
     */
    private final BroadcastReceiver mainLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // extract the location info in the broadcast
            locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            // refresh the display with it
            Timber.d("got location: " + locationInfo);
            waveMapView.setCurrentLocation(locationInfo);
        }
    };
}
