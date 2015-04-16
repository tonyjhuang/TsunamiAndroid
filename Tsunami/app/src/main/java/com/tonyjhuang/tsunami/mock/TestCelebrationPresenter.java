package com.tonyjhuang.tsunami.mock;

import android.os.Bundle;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.ui.main.MainView;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
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
    public void onSplash(Wave wave) {

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
    public void onNoWavesSwipedUp() {

    }

    @Override
    public void onNoWavesSwipedDown() {

    }

    @Override
    public void onErrorSwipedUp() {

    }

    @Override
    public void onErrorSwipedDown() {

    }

    @Override
    public void onBeginSplashButtonClicked() {
        contentView.showSplash();
        mapView.showSplashing(true);
    }

    @Override
    public void onCancelSplashButtonClicked() {
        contentView.showContent(null);
        mapView.showSplashing(false);
    }

    @Override
    public void onSendSplashButtonClicked() {
        contentView.scrollUpOffscreen();

    }

    private void finishSplash() {
        mapView.animateSplash(this::onCancelSplashButtonClicked);
        mainView.showCelebration();
    }

    @Override
    public void onProfileButtonClicked() {
        mainView.showCelebration();
    }

    @Override
    public void onLocationUpdate(LocationInfo newLocationInfo) {
        mapView.setLocationInfo(newLocationInfo);
        onBeginSplashButtonClicked();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outParcel) {

    }
}
