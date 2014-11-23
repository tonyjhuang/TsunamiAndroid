package com.tonyjhuang.tsunami.ui.main.wave.mapview;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by tonyjhuang on 10/27/14.
 */
public class WaveMapViewImpl implements WaveMapView {
    private final double LAT_MIN = -85.0;
    private final double LAT_MAX = 85.0;
    private final double LON_MIN = -180.0;
    private final double LON_MAX = 180.0;

    private final int RIPPLE_RADIUS = 3000;

    /**
     * Our View Presenter
     */
    private WavePresenter presenter;

    /**
     * The wave that we should be displaying right now.
     */
    private Wave wave;

    private ArrayList<Circle> waveRipples = new ArrayList<Circle>();

    /**
     * Our Random seed that will help us make mock data.
     */
    private Random random = new Random();

    /**
     * The Google MapFragment that will be hosting our Map.
     */
    private MapFragment mapFragment;

    /**
     * The actual map instance that we should be drawing on.
     */
    private GoogleMap map;

    public WaveMapViewImpl() {
    }

    @Override
    public void setPresenter(WavePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayWave(Wave wave) {
        if (mapFragment != null && map != null) {
            clearWave();
            this.wave = wave;

            //LatLng latLng = new LatLng(randomLat(), randomLon());
            LatLng latLng = new LatLng(42.331665 + randomDoubleInRange(-0.025, 0.025), -71.108093 + randomDoubleInRange(-0.025, 0.025));
            drawWave(Arrays.asList(latLng));
            zoomTo(latLng);
        } else {
            throw new RuntimeException("No MapFragment set for this WaveMapView!");
        }
    }


    @Override
    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
        this.map = mapFragment.getMap();
    }

    /**
     * Circle object that represents the user's current location (as far as we can tell).
     */
    private Circle currentLocationMarker;

    @Override
    public void setCurrentLocation(LocationInfo locationInfo) {
        LatLng currentLocation = new LatLng(locationInfo.lastLat, locationInfo.lastLong);

        if (currentLocationMarker == null) {
            currentLocationMarker = map.addCircle(new CircleOptions()
                    .center(currentLocation)
                    .radius(300)
                    .fillColor(Color.argb(220, 0, 100, 0))
                    .strokeColor(Color.argb(150, 0, 0, 0))
                    .zIndex(999));
        } else {
            currentLocationMarker.setCenter(currentLocation);
        }
    }

    private void clearWave() {
        for (Circle ripple : waveRipples) {
            ripple.remove();
        }
        waveRipples.clear();
    }

    private void drawWave(List<LatLng> ripples) {
        for (LatLng ripple : ripples) {
            waveRipples.add(drawCircle(ripple));
        }
    }

    private Circle drawCircle(LatLng center) {
        return map.addCircle(new CircleOptions()
                .center(center)
                .radius(RIPPLE_RADIUS)
                .strokeColor(Color.argb(55, 0, 0, 0))
                .fillColor(Color.argb(150, 135, 206, 250)));
    }

    private void zoomTo(LatLng center) {
        map.animateCamera(CameraUpdateFactory.newLatLng(center));
    }

    /**
     * ========================= utility =========================
     */

    private double randomLat() {
        return randomDoubleInRange(LAT_MIN, LAT_MAX);
    }

    private double randomLon() {
        return randomDoubleInRange(LON_MIN, LON_MAX);
    }

    private double randomDoubleInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
