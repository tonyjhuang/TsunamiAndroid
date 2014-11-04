package com.tonyjhuang.tsunami.ui.main.wave.mapview;

import com.google.android.gms.maps.MapFragment;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;

/**
 * Created by tonyjhuang on 10/27/14.
 */
public interface WaveMapView {
    public void setPresenter(WavePresenter presenter);

    public void displayWave(Wave wave);

    public void setMapFragment(MapFragment mapFragment);

    public void setCurrentLocation(LocationInfo locationInfo);
}
