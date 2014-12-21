package com.tonyjhuang.tsunami.ui.main;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.network.TsunamiApiClient;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;

/**
 * Created by tony on 12/21/14.
 */
public class TestEmitterPresenter implements WavePresenter{
    private MainView mainView;

    @SuppressWarnings("unused")
    public TestEmitterPresenter(TsunamiApiClient api) {
    }

    @Override
    public void setContentView(WaveContentView view) {

    }

    @Override
    public void setMapView(WaveMapView view) {

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

    }

    @Override
    public void onSplashSwipedDown() {

    }

    @Override
    public void onBeginSplashButtonClicked() {
        mainView.showCelebration();
    }

    @Override
    public void onCancelSplashButtonClicked() {

    }

    @Override
    public void onSendSplashButtonClicked() {

    }

    @Override
    public void onProfileButtonClicked() {

    }

    @Override
    public void onLocationUpdate(LocationInfo newLocationInfo) {

    }
}
