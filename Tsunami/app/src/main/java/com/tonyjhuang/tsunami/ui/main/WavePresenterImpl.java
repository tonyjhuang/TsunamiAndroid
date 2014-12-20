package com.tonyjhuang.tsunami.ui.main;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApiClient;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.SplashCard;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WMVFinishSplashingCallback;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WaveMapView;
import com.tonyjhuang.tsunami.utils.RxHelper;

import java.util.List;

/**
 * Created by tony on 12/20/14.
 */
public class WavePresenterImpl implements WavePresenter {

    private TsunamiApiClient api;
    private LocationInfo locationInfo;

    private WaveContentView contentView;
    private WaveMapView mapView;

    private List<Wave> wavesToShow;
    /**
     * Index into wavesToShow. How close are we to the end of the list?
     */
    private int index = 0;

    private boolean loading = false;

    public WavePresenterImpl(TsunamiApiClient api) {
        this.api = api;
    }

    private void displayNewWave() {
        if (index > wavesToShow.size() - 5 && !loading) {
            loading = true;
            fetchNewWaves(locationInfo, false);
        }

        if (index < wavesToShow.size()) {
            displayWave(wavesToShow.get(index));
        }
    }

    private void displayWave(Wave wave) {
        contentView.showContentCard(wave);
        mapView.displayWave(wave);
    }

    private Wave getWaveToShow() {
        return wavesToShow.get(index);
    }

    @Override
    public void onContentSwipedUp() {
        Timber.d("onContentSwipedUp");
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

        api.splash(splashContent.title, locationInfo.lastLat, locationInfo.lastLong).publish().connect();

        mapView.finishSplashing(new WMVFinishSplashingCallback() {
            @Override
            public void onFinishSplashing() {
                Timber.d("in callback..");
                contentView.showContentCard(getWaveToShow());
                mapView.displayWave(getWaveToShow());
            }
        });
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        mapView.cancelSplashing();
        contentView.showContentCard(getWaveToShow());
        mapView.displayWave(getWaveToShow());
    }

    @Override
    public void onCancelSplashButtonClicked() {
        mapView.cancelSplashing();
        contentView.showContentCard(getWaveToShow());
        mapView.displayWave(getWaveToShow());
    }

    @Override
    public void onSendSplashButtonClicked() {

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

                    // Show a new wave if we haven't yet.
                    if (contentView.getContentWave() == null && !contentView.isShowingSplashCard())
                        displayNewWave();
                },
                (error) -> Timber.e(error, "uhoh"));
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
}
