package com.tonyjhuang.tsunami.mock;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.ui.main.MainView;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;

/**
 * Created by tony on 1/4/15.
 */
public class TestScrollPresenter implements WavePresenter {

    private WaveContentView contentView;

    @Override
    public void setContentView(WaveContentView contentView) {
        this.contentView = contentView;
    }

    @Override
    public void setMapView(WaveMapView mapView) {

    }

    @Override
    public void setMainView(MainView mainView) {

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
