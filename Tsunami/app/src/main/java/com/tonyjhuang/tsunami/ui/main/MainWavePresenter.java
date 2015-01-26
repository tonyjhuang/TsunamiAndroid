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
            onNextWave = (wave) -> {
                if (wave == null)
                    contentView.showNoWavesCard();
                else if (!isUserSplashing())
                    displayWave(wave, false);
                else
                    currentWave = wave;
            };
        }

        subscribe(waveProvider.getNextWave(), onNextWave, (error) -> Timber.e(error, "fuck."));
    }

    private void displayWave(Wave wave) {
        displayWave(wave, true);
    }

    private void displayWave(Wave wave, boolean animatePreviousViewDown) {
        currentWave = wave;
        contentView.showContentCard(wave, animatePreviousViewDown);
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

        mapView.finishSplashing(() -> {
            if (currentWave != null)
                displayWave(currentWave, false);
            else
                contentView.showLoading();
        });
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        mainView.hideKeyboard();
        mapView.cancelSplashing();
        if (currentWave != null)
            displayWave(currentWave);
        else
            contentView.showLoading();
    }

    @Override
    public void onCancelSplashButtonClicked() {
        mapView.cancelSplashing();
        if (currentWave != null)
            displayWave(currentWave);
        else
            contentView.showLoading();
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
        contentView.showSplashCard();
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

        if (memento.isSplashing) {
            mapView.setLocationInfo(locationInfo);
            onBeginSplashButtonClicked();
        } else {
            if (currentWave != null) {
                displayWave(currentWave);
            } else {
                displayNewWave();
            }
            mapView.setLocationInfo(locationInfo);
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

        public String toString() {
            return TsunamiGson.gson.toJson(this);
        }
    }
}
