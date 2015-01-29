package com.tonyjhuang.tsunami.ui.main.mapview;

import com.google.android.gms.maps.MapFragment;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;

/**
 * Created by tonyjhuang on 10/27/14.
 */
public interface WaveMapView {
    /**
     * Set the presenter for this WaveMapView.
     */
    public void setPresenter(WavePresenter presenter);

    /**
     * Set the MapFragment that this WaveMapView will be drawing on.
     */
    public void setMapFragment(MapFragment mapFragment);

    /**
     * Starting position of the map. Does nothing if there is already a wave or current location
     * being displayed.
     */
    public void setStartingLocation(float lat, float lng);

    /**
     * Display where the user is on the map.
     */
    public void setLocationInfo(LocationInfo locationInfo);

    /**
     * Display the given wave on the map.
     */
    public void displayWave(Wave wave);

    /**
     * Animate in a new ripple, centered around the user's current location.
     */
    public void animateRipple(FinishedRipplingCallback callback);

    public static interface FinishedRipplingCallback {
        public void onFinishRippling();

    }

    /**
     * Display an indicator for the user splash action.
     *
     * @param splashing is the user splashing?
     */
    public void showSplashing(boolean splashing);

    /**
     * Show an animation for the success state of a splash. Also notifies a callback listener
     * if one is passed in.
     *
     * @param callback Callback listener for when the animation is finished. Can be null.
     */
    public void animateSplash(FinishedSplashingCallback callback);

    public static interface FinishedSplashingCallback {
        public void onFinishSplashing();

    }
}
