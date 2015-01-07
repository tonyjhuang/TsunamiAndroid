package com.tonyjhuang.tsunami.ui.main;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.ui.main.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;

/**
 * Created by tonyjhuang on 10/21/14.
 */
public interface WavePresenter {
    public void setContentView(WaveContentView contentView);

    public void setMapView(WaveMapView mapView);

    public void setMainView(MainView mainView);

    public void onContentSwipedUp();

    public void onContentSwipedDown();

    public void onSplashSwipedUp();

    public void onSplashSwipedDown();

    public void onBeginSplashButtonClicked();

    public void onCancelSplashButtonClicked();

    public void onSendSplashButtonClicked();

    public void onProfileButtonClicked();

    public void onLocationUpdate(LocationInfo newLocationInfo);

    public String getMemento();

    public void fromMemento(String string);
}
