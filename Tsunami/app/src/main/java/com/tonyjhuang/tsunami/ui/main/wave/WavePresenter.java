package com.tonyjhuang.tsunami.ui.main.wave;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WaveMapView;

/**
 * Created by tonyjhuang on 10/21/14.
 */
public interface WavePresenter {
    public void setContentView(WaveContentView view);

    public void setMapView(WaveMapView view);

    public void onContentSwipedUp();

    public void onContentSwipedDown();

    public void onSplashSwipedUp();

    public void onSplashSwipedDown();

    public void onSplashButtonClicked();

    public void onLocationUpdate(LocationInfo newLocationInfo);
}
