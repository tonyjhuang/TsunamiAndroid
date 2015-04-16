package com.tonyjhuang.tsunami.ui.main;

import android.os.Bundle;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;

/**
 * Created by tonyjhuang on 10/21/14.
 */
public interface WavePresenter {
    void setContentView(WaveContentView contentView);

    void setMapView(WaveMapView mapView);

    void setMainView(MainView mainView);

    void onContentSwipedUp();

    void onContentSwipedDown();

    void onSplashSwipedUp();

    void onSplashSwipedDown();

    void onNoWavesSwipedUp();

    void onNoWavesSwipedDown();

    void onErrorSwipedUp();

    void onErrorSwipedDown();

    void onSplash(Wave wave);

    void onBeginSplashButtonClicked();

    void onCancelSplashButtonClicked();

    void onSendSplashButtonClicked();

    void onProfileButtonClicked();

    void onLocationUpdate(LocationInfo newLocationInfo);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onSaveInstanceState(Bundle outParcel);

}
