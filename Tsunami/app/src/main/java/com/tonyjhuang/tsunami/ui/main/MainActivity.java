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
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.injection.MainModule;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.GhettoToolbar;
import com.tonyjhuang.tsunami.ui.main.button.SplashButton;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.WaveContentScrollView;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.WaveContentViewScrollListener;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WaveMapView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends TsunamiActivity implements WaveContentViewScrollListener {

    @InjectView(R.id.content_scrollview)
    WaveContentScrollView contentView;
    @InjectView(R.id.splash_button)
    SplashButton splashButton;
    @InjectView(R.id.ghetto_toolbar)
    GhettoToolbar toolbar;

    @Inject
    WavePresenter presenter;
    @Inject
    WaveMapView waveMapView;

    private final String STATE_WAVE = "wave";
    private final String STATE_SPLASHING = "splashing";

    public static void startMainActivity(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<>(super.getModules());
        modules.add(new MainModule(this));
        return modules;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationLibrary.forceLocationUpdate(this);

        /**
         * Create our WaveMapView that will handle manipulating and drawing on our map fragment.
         */
        presenter.setMapView(waveMapView);
        waveMapView.setMapFragment((MapFragment) getFragmentManager().findFragmentById(R.id.map));

        /**
         * Initialize our WaveContentView which will handle the displaying of wave info.
         */
        presenter.setContentView(contentView);
        contentView.setWaveContentViewScrollListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("registering broadcast receiver");
        registerReceiver(mainLocationBroadcastReceiver,
                new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction()));
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("unregistering broadcast receiver");
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
        //savedInstanceState.putParcelable(STATE_WAVE, contentView.getContentWave());
        //savedInstanceState.putBoolean(STATE_SPLASHING, contentView.isShowingSplashCard());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @OnClick(R.id.splash_button)
    public void onSplashButtonClick(View view) {
        presenter.onSplashButtonClicked();
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
}
