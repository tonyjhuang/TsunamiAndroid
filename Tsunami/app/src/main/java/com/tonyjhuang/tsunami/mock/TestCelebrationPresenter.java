package com.tonyjhuang.tsunami.mock;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.ui.main.MainView;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WMVFinishSplashingCallback;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;

/**
 * Created by tony on 12/21/14.
 */
public class TestCelebrationPresenter implements WavePresenter {
    private MainView mainView;
    private WaveMapView mapView;
    private WaveContentView contentView;

    @SuppressWarnings("unused")
    public TestCelebrationPresenter(TsunamiApi api) {
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

    @Override
    public void onContentSwipedUp() {

    }

    @Override
    public void onContentSwipedDown() {

    }

    @Override
    public void onSplashSwipedUp() {
        finishSplash();
    }

    @Override
    public void onSplashSwipedDown() {
        onCancelSplashButtonClicked();
    }

    @Override
    public void onBeginSplashButtonClicked() {
        contentView.showSplashCard();
        mapView.displaySplashing();
    }

    @Override
    public void onCancelSplashButtonClicked() {
        contentView.showContentCard(null);
        mapView.cancelSplashing();
    }

    @Override
    public void onSendSplashButtonClicked() {
        contentView.scrollUpOffscreen();

    }

    private void finishSplash() {
        mapView.finishSplashing(new WMVFinishSplashingCallback() {
            @Override
            public void onFinishSplashing() {
                onCancelSplashButtonClicked();
            }
        });
        mainView.showCelebration();
    }

    @Override
    public void onProfileButtonClicked() {
        mainView.showCelebration();
    }

    @Override
    public void onLocationUpdate(LocationInfo newLocationInfo) {
        mapView.setCurrentLocation(newLocationInfo);
        onBeginSplashButtonClicked();
    }
}
