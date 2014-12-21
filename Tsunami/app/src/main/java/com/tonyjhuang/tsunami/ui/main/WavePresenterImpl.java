package com.tonyjhuang.tsunami.ui.main;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApiClient;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.contentview.SplashCard;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WMVFinishSplashingCallback;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.utils.RxHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 12/20/14.
 */
public class WavePresenterImpl implements WavePresenter {

    private TsunamiApiClient api;
    private LocationInfo locationInfo;

    /**
     * Collection of views that make up the UI for this presenter.
     */
    private WaveContentView contentView;
    private WaveMapView mapView;
    private MainView mainView;

    /**
     * Resident list of waves, we will index through this list as long as it is valid.
     * It should be invalidated on location updates.
     */

    private List<Wave> wavesToShow = new ArrayList<>();
    /**
     * Index into wavesToShow. How close are we to the end of the list?
     */
    private int index = 0;

    /**
     * Are we currently fetching a list of waves from the server?
     */
    private boolean loading = false;

    public WavePresenterImpl(TsunamiApiClient api) {
        this.api = api;
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

    private void displayNewWave() {
        if (index > wavesToShow.size() - 5 && !loading) {
            loading = true;
            fetchNewWaves(locationInfo, false);
        }

        Wave newWave = getWaveToShow();
        if (newWave == null)
            fetchNewWaves(locationInfo, true);
        else
            displayWave(newWave);
    }

    private void displayWave(Wave wave) {
        contentView.showContentCard(wave);
        mapView.displayWave(wave);
    }

    private Wave getWaveToShow() {
        return index < wavesToShow.size() ? wavesToShow.get(index) : null;
    }

    @Override
    public void onContentSwipedUp() {
        Timber.d("onContentSwipedUp");
        api.ripple(contentView.getContentWave().getId(), locationInfo.lastLat, locationInfo.lastLong)
                .publish()
                .connect();

        index++;
        displayNewWave();
    }

    @Override
    public void onContentSwipedDown() {
        Timber.d("onContentSwipedDown");
        index++;
        displayNewWave();
    }

    @Override
    public void onSplashSwipedUp() {
        Timber.d("onSplashSwipedUp");
        SplashCard.SplashContent splashContent = contentView.retrieveSplashContent();
        api.splash(splashContent.title, splashContent.body, locationInfo.lastLat, locationInfo.lastLong)
                .publish()
                .connect();

        mainView.showCelebration();
        mapView.finishSplashing(new WMVFinishSplashingCallback() {
            @Override
            public void onFinishSplashing() {
                Timber.d("in callback..");
                displayNewWave();
            }
        });
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        mapView.cancelSplashing();
        displayNewWave();
    }

    @Override
    public void onCancelSplashButtonClicked() {
        mapView.cancelSplashing();
        displayNewWave();
    }

    @Override
    public void onSendSplashButtonClicked() {
        contentView.scrollUpOffscreen();
    }

    @Override
    public void onProfileButtonClicked() {

    }

    @Override
    public void onBeginSplashButtonClicked() {
        contentView.clearSplashCard();
        contentView.showSplashCard();
        mapView.displaySplashing();
    }

    @Override
    public void onLocationUpdate(LocationInfo newLocationInfo) {
        locationInfo = newLocationInfo;
        Timber.d("locationInfo: lat " + newLocationInfo.lastLat + ", lon " + newLocationInfo.lastLong);
        mapView.setCurrentLocation(locationInfo);

        fetchNewWaves(locationInfo, true);
    }

    /**
     * Retrieve a new list of waves from the backend.
     */
    private void fetchNewWaves(LocationInfo locationInfo, boolean refresh) {
        RxHelper.bindAsync(api.getWaves(locationInfo.lastLat, locationInfo.lastLong),
                (List<Wave> waves) -> {
                    loading = false;
                    if (refresh) {
                        wavesToShow = waves;
                        index = 0;
                    } else {
                        wavesToShow.addAll(waves);
                    }

                    Timber.d("contentView wave: " + contentView.getContentWave());
                    // Show a new wave if we haven't yet.
                    if (contentView.getContentWave() == null && !contentView.isShowingSplashCard())
                        displayNewWave();
                },
                (error) -> Timber.e(error, "uhoh"));
    }
}
