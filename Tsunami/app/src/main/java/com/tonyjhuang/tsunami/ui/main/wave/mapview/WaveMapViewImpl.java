package com.tonyjhuang.tsunami.ui.main.wave.mapview;

import android.content.res.Resources;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.injection.Injector;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;

import java.util.ArrayList;
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
     * Marker that represents the user's current location (as far as we can tell).
     */
    private Marker currentLocationMarker;

    /**
     * User's current location.
     */
    private LatLng currentLocation;

    /**
     * Shape to display while the user is splashing.
     */
    private Circle splashingIndicator;


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

            if (splashingIndicator != null) {
                splashingIndicator.setVisible(false);
            }

            this.wave = wave;

            LatLng last = null;
            ArrayList<LatLng> ripples = new ArrayList<LatLng>();
            for (int i = 0; i < random.nextInt(10) + 1; i++) {
                if (last == null) {
                    if (currentLocation == null) {
                        last = getRandomLatLng(42.331665, -71.108093);
                    } else {
                        last = getRandomLatLng(currentLocation.latitude, currentLocation.longitude);
                    }
                } else {
                    last = getRandomLatLng(last.latitude, last.longitude);
                }
                ripples.add(last);
            }
            drawRipples(ripples);
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
        zoomTo(currentLocation);

        if (currentLocationMarker == null) {
            BitmapDescriptor markerBitmap = BitmapDescriptorFactory.fromResource(R.drawable.current_location);
            currentLocationMarker = map.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .anchor(0.5f, 0.5f)
                    .icon(markerBitmap));
        } else {
            currentLocationMarker.setPosition(currentLocation);
        }

        if(splashingIndicator != null) {
            splashingIndicator.setCenter(currentLocation);
        }
    }

    @Override
    public void displaySplashing() {
        clearRipples();
        if (currentLocation == null) {
                /* Uh oh, it looks like the user has tried to splash content with a location*/
        } else {
            zoomTo(currentLocation);
            if (splashingIndicator == null) {
                splashingIndicator = map.addCircle(new CircleOptions()
                        .center(currentLocation)
                        .radius(RIPPLE_RADIUS)
                        .fillColor(resources.getColor(R.color.content_view_map_splashing_fill))
                        .strokeColor(resources.getColor(R.color.content_view_map_splashing_stroke)));
            } else {
                splashingIndicator.setCenter(currentLocation);
            }

            splashingIndicator.setVisible(true);
        }

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

    private LatLng getRandomLatLng(double lat, double lng) {
        return new LatLng(
                lat + randomDoubleInRange(-0.025, 0.025),
                lng + randomDoubleInRange(-0.025, 0.025));
    }
}
