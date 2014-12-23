package com.tonyjhuang.tsunami.ui.main.mapview;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.animation.OvershootInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.utils.SimpleAnimatorListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjhuang on 10/27/14.
 */
public class WaveMapViewImpl implements WaveMapView {
    private static final int RIPPLE_RADIUS = 3000;
    private static final int FINISH_SPLASH_ANIMATION_DURATION = 3000;

    private Resources resources;

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
    private ArrayList<Circle> waveRipples = new ArrayList<>();

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

    /**
     * Animator that pulsates the splashingIndicators radius.
     */
    private ValueAnimator splashingIndicatorRadiusAnimator;

    public WaveMapViewImpl(Resources resources) {
        this.resources = resources;
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

            drawRipples(wave.getRipples());
            zoomToFit(waveRipples);
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
            BitmapDescriptor markerBitmap = BitmapDescriptorFactory.fromResource(R.drawable.current_location);
            currentLocationMarker = map.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .anchor(0.5f, 0.5f)
                    .icon(markerBitmap));
        } else {
            currentLocationMarker.setPosition(currentLocation);
        }

        if (splashingIndicator != null) {
            splashingIndicator.setCenter(currentLocation);
        }
    }


    @Override
    public void displaySplashing() {
        clearRipples();
        if (currentLocation == null) {
                /* Uh oh, it looks like the user has tried to splash content without a location*/
        } else {
            int strokeColor = resources.getColor(R.color.content_view_map_splashing_stroke);
            zoomTo(currentLocation, 12);
            if (splashingIndicator == null) {
                splashingIndicator = map.addCircle(new CircleOptions()
                        .center(currentLocation)
                        .radius(RIPPLE_RADIUS)
                        .strokeColor(strokeColor));

                splashingIndicatorRadiusAnimator = ValueAnimator.ofInt(RIPPLE_RADIUS + 100, RIPPLE_RADIUS)
                        .setDuration(1000);
                splashingIndicatorRadiusAnimator.setRepeatMode(ValueAnimator.REVERSE);
                splashingIndicatorRadiusAnimator.setRepeatCount(ValueAnimator.INFINITE);
                splashingIndicatorRadiusAnimator.addUpdateListener((ValueAnimator animator) -> {
                    splashingIndicator.setRadius((Integer) animator.getAnimatedValue());
                });
            } else {
                splashingIndicator.setCenter(currentLocation);
            }

            splashingIndicatorRadiusAnimator.start();
            splashingIndicator.setVisible(true);
        }
    }

    @Override
    public void finishSplashing(final WMVFinishSplashingCallback callback) {
        if (splashingIndicatorRadiusAnimator != null) {
            splashingIndicatorRadiusAnimator.cancel();
        }
        final Integer colorFrom = resources.getColor(R.color.content_view_map_splashing_fill_begin);
        final Integer colorTo = resources.getColor(R.color.content_view_map_splashing_fill_end);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setInterpolator(new OvershootInterpolator());
        colorAnimation.addUpdateListener((ValueAnimator animator) ->
                        splashingIndicator.setFillColor((Integer) animator.getAnimatedValue())
        );
        colorAnimation.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                splashingIndicator.setVisible(false);
                splashingIndicator.setFillColor(colorFrom);
                callback.onFinishSplashing();
            }
        });
        colorAnimation.setDuration(FINISH_SPLASH_ANIMATION_DURATION);
        colorAnimation.start();
    }

    @Override
    public void cancelSplashing() {
        if (splashingIndicatorRadiusAnimator != null) {
            splashingIndicatorRadiusAnimator.cancel();
        }
        if(splashingIndicator != null) {
            splashingIndicator.setVisible(false);
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
    private void drawRipples(List<Ripple> ripples) {
        for (Ripple ripple : ripples) {
            LatLng latLng = new LatLng(ripple.getLatitude(), ripple.getLongitude());
            waveRipples.add(drawLatLng(latLng));
        }
    }

    /**
     * Draws a ripple represented by a LatLng at center
     */
    private Circle drawLatLng(LatLng center) {
        return map.addCircle(new CircleOptions()
                .center(center)
                .radius(RIPPLE_RADIUS)
                .fillColor(resources.getColor(R.color.content_view_map_ripple_fill))
                .strokeColor(resources.getColor(R.color.content_view_map_ripple_stroke)));
    }

    /**
     * Zoom to a specific LatLng with zoom level.
     */
    private void zoomTo(LatLng center, int zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));
    }

    private void zoomToFit(List<Circle> ripples) {
        if (ripples == null || ripples.size() == 0)
            return;
        double minLat = currentLocation == null ? 999 : currentLocation.latitude - 0.025;
        double minLng = currentLocation == null ? 999 : currentLocation.longitude - 0.025;
        double maxLat = currentLocation == null ? -999 : currentLocation.latitude + 0.025;
        double maxLng = currentLocation == null ? -999 : currentLocation.longitude + 0.025;
        for (Circle ripple : ripples) {
            minLat = Math.min(minLat, ripple.getCenter().latitude - 0.05);
            minLng = Math.min(minLng, ripple.getCenter().longitude - 0.05);
            maxLat = Math.max(maxLat, ripple.getCenter().latitude + 0.05);
            maxLng = Math.max(maxLng, ripple.getCenter().longitude + 0.05);
        }

        if (map != null) {
            final CameraUpdate update = CameraUpdateFactory.newLatLngBounds(
                    new LatLngBounds(
                            new LatLng(minLat, minLng),
                            new LatLng(maxLat, maxLng)
                    ), 0);
            try {
                map.animateCamera(update);
            } catch (IllegalStateException e) {
                map.setOnCameraChangeListener((cameraPosition) -> {
                    // Move camera.
                    map.animateCamera(update);
                    // Remove listener to prevent position reset on camera move.
                    map.setOnCameraChangeListener(null);
                });
            }
        }
    }

}
