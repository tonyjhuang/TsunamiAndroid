package com.tonyjhuang.tsunami.ui.main.wave.mapview;

import android.content.res.Resources;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.injection.Injector;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by tonyjhuang on 10/27/14.
 */
public class WaveMapViewImpl implements WaveMapView {
    private static final int RIPPLE_RADIUS = 3000;

    @Inject
    Resources resources;


    /**
     * The Google MapFragment that will be hosting our Map.
     */
    private MapFragment mapFragment;

    /**
     * The actual map instance that we should be drawing on.
     */
    private GoogleMap map;

    /**
     * Our View Presenter
     */
    private WavePresenter presenter;

    /**
     * The wave that we should be displaying right now.
     */
    private Wave wave;

    /**
     * List of Ripple Circles currently drawn on the map.
     */
    private ArrayList<Circle> waveRipples = new ArrayList<Circle>();

    /**
     * Our Random seed that will help us make mock data.
     */
    private Random random = new Random();

    /**
     * Circle object that represents the user's current location (as far as we can tell).
     */
    private Circle currentLocationMarker;

    /**
     * User's current location.
     */
    private LatLng currentLocation;


    public WaveMapViewImpl(Injector injector) {
        injector.inject(this);
    }

    @Override
    public void setPresenter(WavePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayWave(Wave wave) {
        if (mapFragment != null && map != null) {
            clearRipples();
            this.wave = wave;

            LatLng latLng;
            if (currentLocation == null) {
                latLng = new LatLng(
                        42.331665 + randomDoubleInRange(-0.025, 0.025),
                        -71.108093 + randomDoubleInRange(-0.025, 0.025));
            } else {
                latLng = new LatLng(
                        currentLocation.latitude + randomDoubleInRange(-0.025, 0.025),
                        currentLocation.longitude + randomDoubleInRange(-0.025, 0.025));
            }
            drawRipples(Arrays.asList(latLng));
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

    @Override
    public void setCurrentLocation(LocationInfo locationInfo) {
        currentLocation = new LatLng(locationInfo.lastLat, locationInfo.lastLong);

        if (currentLocationMarker == null) {
            currentLocationMarker = map.addCircle(new CircleOptions()
                    .center(currentLocation)
                    .radius(300)
                    .fillColor(resources.getColor(R.color.content_view_map_current_location_fill))
                    .strokeColor(resources.getColor(R.color.content_view_map_current_location_stroke))
                    .zIndex(999));
        } else {
            currentLocationMarker.setCenter(currentLocation);
        }
    }

    @Override
    public void displaySplashing() {

    }

    /* ========== DRAWING UTILITY FUNCTIONS =========== */

    /**
     * Remove all ripples from the map and forget them.
     */
    private void clearRipples() {
        for (Circle ripple : waveRipples) {
            ripple.remove();
        }
        waveRipples.clear();
    }

    /**
     * Adds/Draws a list of LatLngs as ripples to the map.
     */
    private void drawRipples(List<LatLng> ripples) {
        for (LatLng ripple : ripples) {
            waveRipples.add(drawRipple(ripple));
        }
    }

    /**
     * Draws a ripple represented by a LatLng at center
     */
    private Circle drawRipple(LatLng center) {
        return map.addCircle(new CircleOptions()
                .center(center)
                .radius(RIPPLE_RADIUS)
                .fillColor(resources.getColor(R.color.content_view_map_ripple_fill))
                .strokeColor(resources.getColor(R.color.content_view_map_ripple_stroke)));
    }

    /**
     * Zoom to a specific LatLng.
     */
    private void zoomTo(LatLng center) {
        map.animateCamera(CameraUpdateFactory.newLatLng(center));
    }

    /**
     * ========================= utility =========================
     */

    private double randomDoubleInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
