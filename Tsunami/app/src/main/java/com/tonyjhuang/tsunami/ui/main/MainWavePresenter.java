package com.tonyjhuang.tsunami.ui.main;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.contentview.SplashCard;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.utils.RxHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Couple notes on how/when we retrieve a new list of waves from the api:
 * Whenever
 */
public class MainWavePresenter implements WavePresenter {
    // How close do we want to run to the end of the list of stored waves before we ask for more?
    public static final int BUFFER_SIZE = 5;

    private TsunamiApi api;
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
     * Index into wavesToShow.
     */
    private int index = 0;

    /**
     * Are we currently fetching a list of waves from the server?
     */
    private boolean loading = false;

    public MainWavePresenter(TsunamiApi api) {
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

    /**
     * By 'new', we mean appropriate, and by that we mean whatever wave 'index' is pointed to.
     * We let other methods handle the index, we just pass the current wave to our views. Here,
     * we also make sure that our
     */
    private void displayNewWave() {
        if (index > wavesToShow.size() - BUFFER_SIZE && !loading) {
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

        contentView.clearContentWave();
        index++;

        mapView.displayRipple(this::displayNewWave);

    }

    @Override
    public void onContentSwipedDown() {
        Timber.d("onContentSwipedDown");
        api.dismissWave(getWaveToShow().getId()).publish().connect();

        contentView.clearContentWave();
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
        mainView.hideKeyboard();

        mapView.finishSplashing(this::displayNewWave);
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        mainView.hideKeyboard();
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
        Timber.d(String.format("new location : %f, %f", newLocationInfo.lastLat, newLocationInfo.lastLong));
        if (locationInfo != null &&
                locationInfo.lastLat == newLocationInfo.lastLat
                && locationInfo.lastLong == newLocationInfo.lastLong)
            return;

        locationInfo = newLocationInfo;
        mapView.setCurrentLocation(locationInfo);

        invalidateWaves(newLocationInfo);
        if (index + BUFFER_SIZE >= wavesToShow.size())
            fetchNewWaves(locationInfo, false);
    }

    /**
     * Given a new LocationInfo representing the user's new location, prune waves out of
     * our current list of wavesToShow that no longer apply to their new location.
     * <p>
     * TODO: use accuracy.
     */
    private void invalidateWaves(LocationInfo newLocationInfo) {
        double lat = newLocationInfo.lastLat;
        double lon = newLocationInfo.lastLong;

        Wave currentWave = getWaveToShow();
        Iterator<Wave> iterator = wavesToShow.iterator();

        while (iterator.hasNext()) {
            Wave next = iterator.next();
            if (next.equals(currentWave)) // Don't delete the current wave
                continue;

            if (!next.isValidFor(lat, lon))
                iterator.remove();
        }

        index = 0; // Reset index and set it to the correct new value.
        for (Wave wave : wavesToShow) {
            if (wave.equals(currentWave))
                return;
            else
                index++;
        }
    }

    /**
     * Retrieve a new list of waves from the backend.
     *
     * @param refresh: start from scratch? will delete all current waves and reset index to 0
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
}
