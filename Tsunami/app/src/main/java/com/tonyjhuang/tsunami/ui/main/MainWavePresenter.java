package com.tonyjhuang.tsunami.ui.main;

import com.google.gson.annotations.Expose;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.api.parsers.TsunamiGson;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.splash.SplashContent;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;

import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
        return contentView.getCurrentViewType().equals(WaveContentView.ViewType.SPLASHING);
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
                        displayWave(wave, false);
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

    @Override
    public String getMemento() {
        MainWavePresenterMemento memento = new MainWavePresenterMemento();
        memento.isSplashing = isUserSplashing();
        memento.viewType = contentView.getCurrentViewType();
        memento.waveProviderMemento = waveProvider.getMemento();
        memento.currentWave = currentWave;
        if (locationInfo != null) {
            memento.lastLat = locationInfo.lastLat;
            memento.lastLong = locationInfo.lastLong;
        }
        memento.hasLatLong = locationInfo != null;
        return memento.toString();
    }

    @Override
    public void fromMemento(String string) {
        MainWavePresenterMemento memento =
                TsunamiGson.gson.fromJson(string, MainWavePresenterMemento.class);

        waveProvider.fromMemento(memento.waveProviderMemento);
        currentWave = memento.currentWave;
        firstRun = false;

        if (memento.hasLatLong) {
            if (locationInfo == null) locationInfo = new LocationInfo(activity);
            locationInfo.lastLat = memento.lastLat;
            locationInfo.lastLong = memento.lastLong;
        }
        mapView.setLocationInfo(locationInfo);

        // Recreate contentview with proper state.
        switch (memento.viewType) {
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
                break;
        }
    }

    private static class MainWavePresenterMemento {
        @Expose
        boolean isSplashing;
        @Expose
        String waveProviderMemento;
        @Expose
        Wave currentWave;
        @Expose
        float lastLat;
        @Expose
        float lastLong;
        @Expose
        boolean hasLatLong;
        @Expose
        WaveContentView.ViewType viewType;

        public String toString() {
            return TsunamiGson.gson.toJson(this);
        }
    }
}
