package com.tonyjhuang.tsunami.ui.main.mapview;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Handler;
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
    private static final int RIPPLE_RADIUS = 2400;
    private static final int FINISH_SPLASH_ANIMATION_DURATION = 1000;
    // How long to wait after the animation has finished to notify the callback.
    private static final int FINISH_SPLASH_ANIMATION_POST_DELAY = 1500;

    private static final int DISPLAY_RIPPLE_ANIMATION_DURATION = 500;
    private static final int DISPLAY_RIPPLE_ANIMATION_POST_DELAY = 250;

    private static final int MAX_ZOOM = 13;

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

    /**
     * Are we currently splashing?
     */
    private boolean splashing;

    public WaveMapViewImpl(Resources resources) {
        this.resources = resources;
    }

    @Override
    public void setPresenter(WavePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayWave(Wave wave) {
        splashing = false;
        if (mapFragment != null && map != null) {
            clearRipples();
            this.wave = wave;

            if (wave != null) {
                drawRipples(wave.getRipples(), wave.getSplashId());
                zoomToFit(waveRipples);
            } else {
                if (currentLocation != null) {
                    zoomTo(currentLocation, MAX_ZOOM);
                }
            }
        } else {
            throw new RuntimeException("No MapFragment set for this WaveMapView!");
        }
    }

    @Override
    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
        this.map = mapFragment.getMap();
        if (pendingStartLat != -1 && pendingStartLng != -1) {
            zoomTo(new LatLng(pendingStartLat, pendingStartLng), MAX_ZOOM);
        }
    }

    float pendingStartLat = -1, pendingStartLng = -1;

    public void setStartingLocation(float lat, float lng) {
        if (waveRipples.size() == 0 && currentLocation == null) {
            if (map != null) {
                zoomTo(new LatLng(lat, lng), MAX_ZOOM);
            } else {
                pendingStartLat = lat;
                pendingStartLng = lng;
            }
        }
    }

    @Override
    public void setLocationInfo(LocationInfo locationInfo) {
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

        if (!splashing) {
            if (waveRipples.size() == 0)
                zoomTo(currentLocation, MAX_ZOOM);
            else
                zoomToFit(waveRipples);
        } else {
            displaySplashing();
        }
    }


    @Override
    public void displaySplashing() {
        splashing = true;
        clearRipples();
        if (currentLocation == null) {
                /* Uh oh, it looks like the user has tried to splash content without a location*/
        } else {
            int strokeColor = resources.getColor(R.color.map_view_splashing_stroke);
            int fillColor = resources.getColor(R.color.map_view_splashing_fill_begin);

            zoomTo(currentLocation, MAX_ZOOM);

            if (splashingIndicator == null) {
                addSplashingIndicator(strokeColor);
            } else {
                splashingIndicator.setCenter(currentLocation);
            }

            splashingIndicator.setFillColor(fillColor);
            if(!splashingIndicatorRadiusAnimator.isRunning())
                splashingIndicatorRadiusAnimator.start();
            splashingIndicator.setVisible(true);
        }
    }

    /**
     * Adds a pulsating circle outline to the map to represent the current user's splash radius
     */
    private void addSplashingIndicator(int strokeColor) {
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
    }

    @Override
    public void finishSplashing(final FinishedSplashingCallback callback) {
        if (splashingIndicatorRadiusAnimator != null) {
            splashingIndicatorRadiusAnimator.cancel();
        }
        int startColor = resources.getColor(R.color.map_view_splashing_fill_begin);
        int endColor = resources.getColor(R.color.map_view_splashing_fill_end);

        ValueAnimator colorAnimator =
                createCircleColorAnimator(splashingIndicator, startColor, endColor, FINISH_SPLASH_ANIMATION_DURATION);

        colorAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                new Handler().postDelayed(() -> {
                    if (splashingIndicator != null) {
                        splashingIndicator.setVisible(false);
                    }
                    if (callback != null) {
                        callback.onFinishSplashing();
                    }
                }, FINISH_SPLASH_ANIMATION_POST_DELAY);
            }
        });
        colorAnimator.start();
    }

    @Override
    public void cancelSplashing() {
        if (splashingIndicatorRadiusAnimator != null) {
            splashingIndicatorRadiusAnimator.cancel();
        }
        if (splashingIndicator != null) {
            splashingIndicator.setVisible(false);
        }
    }

    @Override
    public void displayRipple(FinishedRipplingCallback callback) {
        int startColor = resources.getColor(R.color.map_view_ripple_new_fill_begin);
        int endColor = resources.getColor(R.color.map_view_ripple_fill);
        int strokeColor = resources.getColor(R.color.map_view_ripple_new_stroke);

        Circle rippleCircle = drawLatLng(currentLocation, startColor, strokeColor);
        waveRipples.add(rippleCircle);
        ValueAnimator colorAnimator =
                createCircleColorAnimator(rippleCircle, startColor, endColor, DISPLAY_RIPPLE_ANIMATION_DURATION);
        if (callback != null) {
            colorAnimator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    new Handler().postDelayed(callback::onFinishRippling, DISPLAY_RIPPLE_ANIMATION_POST_DELAY);
                }
            });
        }

        colorAnimator.start();
    }

    private ValueAnimator createCircleColorAnimator(Circle circle, int startColor, int endColor, int duration) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimation.setInterpolator(new OvershootInterpolator(3.0f));
        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(
                (ValueAnimator animator) -> {
                    if (circle != null)
                        circle.setFillColor((Integer) animator.getAnimatedValue());
                }
        );
        return colorAnimation;
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
    private void drawRipples(List<Ripple> ripples, long splashId) {
        int fillColor = resources.getColor(R.color.map_view_ripple_fill);
        int strokeColor = resources.getColor(R.color.map_view_ripple_stroke);
        int splashStrokeColor = resources.getColor(R.color.map_view_ripple_stroke_splash);
        for (Ripple ripple : ripples) {
            LatLng latLng = new LatLng(ripple.getLatitude(), ripple.getLongitude());
            waveRipples.add(drawLatLng(latLng,
                    fillColor,
                    ripple.getId() == splashId ? splashStrokeColor : strokeColor));
        }
    }

    /**
     * Draws a ripple represented by a LatLng at center
     */
    private Circle drawLatLng(LatLng center, int fillColor, int strokeColor) {
        return map.addCircle(new CircleOptions()
                .center(center)
                .radius(RIPPLE_RADIUS)
                .fillColor(fillColor)
                .strokeColor(strokeColor));
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
