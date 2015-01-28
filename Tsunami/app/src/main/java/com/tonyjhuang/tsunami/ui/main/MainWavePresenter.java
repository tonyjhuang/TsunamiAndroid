package com.tonyjhuang.tsunami.ui.main;

import android.os.Bundle;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.splash.SplashContent;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;

import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView.ViewType;

/**
 * TODO: need to clean up interaction with loading.
 */
public class MainWavePresenter implements WavePresenter {
    private TsunamiApi api;
    private TsunamiActivity activity;
    private LocationInfo locationInfo;
    private WaveProvider waveProvider;

    /**
     * Collection of views that make up the UI for this presenter.
     */
    private WaveContentView contentView;
    private WaveMapView mapView;
    private MainView mainView;

    private Wave currentWave;
    private boolean firstRun = true;
    private boolean loadingNextWave = false;

    public MainWavePresenter(TsunamiApi api, TsunamiActivity activity, WaveProvider waveProvider) {
        this.api = api;
        this.activity = activity;
        this.waveProvider = waveProvider;
    }

    @Override
    public void setContentView(WaveContentView contentView) {
        this.contentView = contentView;
        contentView.setPresenter(this);
    }

    @Override
    public void setMapView(WaveMapView mapView) {
        this.mapView = mapView;
        mapView.setPresenter(this);
    }

    @Override
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    private boolean isUserSplashing() {
        return contentView.getCurrentViewType().equals(ViewType.SPLASHING);
    }

    /**
     * Grab a new wave from the provider. If we have an appropriate Wave sitting in 'currentWave'
     * (i.e. cached during splashing), you should be calling #displayWave instead.
     */
    private void displayNewWave() {
        Action1<Wave> onNextWave;
        if (waveProvider.hasNextWave()) {
            /**
             * Synchronous, we have a wave ready so let's show it if the user isn't splashing.
             */
            onNextWave = (wave) -> {
                if (!isUserSplashing())
                    displayWave(wave);
                else
                    currentWave = wave;
            };
        } else {
            /**
             * Async, we need to wait til the network returns to show a wave.
             * We also need to make sure we respect the user's splashing state. Don't show them
             * a wave if they are currently splashing!
             */
            if (!isUserSplashing()) contentView.showLoading();
            mapView.displayWave(null);
            loadingNextWave = true;
            onNextWave = (wave) -> {
                loadingNextWave = false;
                if (!isUserSplashing()) {
                    if (wave == null) {
                        contentView.showNoWaves();
                    } else {
                        displayWave(wave);
                    }
                } else {
                    currentWave = wave;
                }
            };
        }

        // TODO: show error card view.
        subscribe(waveProvider.getNextWave(), onNextWave, this::onGetWaveError);
    }

    private void onGetWaveError(Throwable error) {
        if (!isUserSplashing())
            contentView.showError();
    }

    private void displayWave(Wave wave) {
        displayWave(wave, true);
    }

    private void displayWave(Wave wave, boolean animatePreviousViewDown) {
        currentWave = wave;
        contentView.showContent(wave, animatePreviousViewDown);
        mapView.displayWave(wave);
    }

    @Override
    public void onContentSwipedUp() {
        Timber.d("onContentSwipedUp");
        Wave wave = contentView.getContentWave();
        contentView.clearContentWave();

        if (wave == null) return;
        if (wave.isValidFor(locationInfo.lastLat, locationInfo.lastLong)) {
            api.ripple(wave.getId(), locationInfo.lastLat, locationInfo.lastLong)
                    .publish()
                    .connect();
            contentView.hideContent();
            mapView.displayRipple(this::displayNewWave);
        } else {
            displayNewWave();
        }

    }

    @Override
    public void onContentSwipedDown() {
        Timber.d("onContentSwipedDown");
        if (waveProvider.hasNextWave()) {
            api.dismissWave(currentWave.getId()).publish().connect();
            displayNewWave();
        } else {
            contentView.showLoading();
            subscribe(api.dismissWave(currentWave.getId()),
                    (aVoid) -> displayNewWave(),
                    (error) -> Timber.e(error, "couldnt dismiss"));
        }
    }

    @Override
    public void onSplashSwipedUp() {
        Timber.d("onSplashSwipedUp");
        SplashContent splashContent = contentView.retrieveSplashContent();
        api.splash(splashContent.title,
                splashContent.body,
                splashContent.contentType,
                locationInfo.lastLat,
                locationInfo.lastLong)
                .publish()
                .connect();

        mainView.showCelebration();
        mainView.hideKeyboard();

        mapView.finishSplashing(this::finishSplash);
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        mainView.hideKeyboard();
        mapView.cancelSplashing();
        finishSplash();
    }

    /**
     * Call after the user has either successfully splashed or cancelled a splash. Resets the
     * contentview to the appropriate state.
     */
    private void finishSplash() {
        if (currentWave != null) {
            displayWave(currentWave, false);
        } else if (loadingNextWave) {
            // if we're loading the next wave, show the loader and wait.
            contentView.showLoading();
        } else {
            // otherwise, show the loader and ask for a new wave.
            contentView.showLoading();
            displayNewWave();
        }
    }

    //TODO: make this fun
    @Override
    public void onNoWavesSwipedUp() {
        Timber.d("onNoWavesSwipedUp");
        displayNewWave();
    }

    @Override
    public void onNoWavesSwipedDown() {
        Timber.d("onNoWavesSwipedDown");
        displayNewWave();
    }

    @Override
    public void onErrorSwipedUp() {
        Timber.d("onErrorSwipedUp");
        displayNewWave();
    }

    @Override
    public void onErrorSwipedDown() {
        Timber.d("onErrorSwipedDown");
        displayNewWave();
    }

    @Override
    public void onCancelSplashButtonClicked() {
        mapView.cancelSplashing();
        contentView.scrollDownOffscreen();
    }

    @Override
    public void onSendSplashButtonClicked() {
        contentView.scrollUpOffscreen();
    }

    @Override
    public void onProfileButtonClicked() {
        mainView.openProfileView();
    }

    @Override
    public void onBeginSplashButtonClicked() {
        contentView.clearSplashCard();
        contentView.showSplash();
        mapView.displaySplashing();
    }

    @Override
    public void onLocationUpdate(LocationInfo newLocationInfo) {
        Timber.d("onLocationUpdate!");
        locationInfo = newLocationInfo;
        mapView.setLocationInfo(locationInfo);
        waveProvider.setLocationInfo(locationInfo);

        if (firstRun) displayNewWave();
        firstRun = false;
    }

    private <T> void subscribe(Observable<T> observable, Action1<T> onNext, Action1<Throwable> onError) {
        AndroidObservable.bindActivity(activity, observable)
                .subscribeOn(Schedulers.newThread())
                .subscribe(onNext, onError);
    }


    /* Save state */

    private static final String STATE_VIEW_TYPE = "MainWavePresenter_view_type";
    private static final String STATE_CURRENT_WAVE = "MainWavePresenter_current_wave";
    private static final String STATE_LAST_LAT = "MainWavePresenter_last_lat";
    private static final String STATE_LAST_LNG = "MainWavePresenter_last_long";
    private static final String STATE_HAS_LAT_LNG = "MainWavePresenter_has_lat_long";

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        ViewType viewType = (ViewType) savedInstanceState.getSerializable(STATE_VIEW_TYPE);

        waveProvider.onRestoreInstanceState(savedInstanceState);
        currentWave = (Wave) savedInstanceState.getSerializable(STATE_CURRENT_WAVE);
        firstRun = false;

        if (savedInstanceState.getBoolean(STATE_HAS_LAT_LNG, false)) {
            if (locationInfo == null) locationInfo = new LocationInfo(activity);
            locationInfo.lastLat = savedInstanceState.getFloat(STATE_LAST_LAT);
            locationInfo.lastLong = savedInstanceState.getFloat(STATE_LAST_LNG);
        }

        // Workaround: setting the mapview's location focuses the window size on the current location,
        // IF we don't have a current wave..
        // If we have a wave, let's set the wave view first, to avoid pinponging the map.
        if (!viewType.equals(ViewType.CONTENT))
            mapView.setLocationInfo(locationInfo);

        // Recreate contentview with proper state.
        switch (viewType) {
            case NO_WAVES:
                contentView.showNoWaves();
                break;
            case ERROR:
                contentView.showError();
                break;
            case SPLASHING:
                onBeginSplashButtonClicked();
                break;
            case CONTENT:
                if (currentWave != null)
                    displayWave(currentWave);
                else
                    displayNewWave();
                mapView.setLocationInfo(locationInfo);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outParcel) {
        outParcel.putSerializable(STATE_VIEW_TYPE, contentView.getCurrentViewType());
        outParcel.putSerializable(STATE_CURRENT_WAVE, currentWave);
        if (locationInfo != null) {
            outParcel.putFloat(STATE_LAST_LAT, locationInfo.lastLat);
            outParcel.putFloat(STATE_LAST_LNG, locationInfo.lastLong);
            outParcel.putBoolean(STATE_HAS_LAT_LNG, true);
        }

        waveProvider.onSaveInstanceState(outParcel);
    }
}
