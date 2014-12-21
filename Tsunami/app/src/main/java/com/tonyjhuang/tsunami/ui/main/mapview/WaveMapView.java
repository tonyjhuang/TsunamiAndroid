package com.tonyjhuang.tsunami.ui.main.mapview;

import com.google.android.gms.maps.MapFragment;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;

/**
 * Created by tonyjhuang on 10/27/14.
 */
public interface WaveMapView {
    public void setPresenter(WavePresenter presenter);

    /**
     * Display the given wave on the map.
     */
    public void displayWave(Wave wave);

    /**
     * Show that the user is in the process of splashing.
     */
    public void displaySplashing();

    /**
     * The user wants to splash their content.
     */
    public void finishSplashing(WMVFinishSplashingCallback callback);

    /**
     * The user has cancelled the splash.
     */
    public void cancelSplashing();

    /**
     * Set the MapFragment that this WaveMapView will be drawing on.
     */
    public void setMapFragment(MapFragment mapFragment);

    /**
     * Should probably display where the user is on the map.
     */
    public void setCurrentLocation(LocationInfo locationInfo);
}
