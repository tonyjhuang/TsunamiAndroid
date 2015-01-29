package com.tonyjhuang.tsunami.ui.main.mapview;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by tonyjhuang on 10/27/14.
 */
public class MainWaveMapView implements WaveMapView {

    private static final int RIPPLE_RADIUS = 2400; // in meters

    private static final int RADIUS_PULSE_ANIMATION_DURATION = 1000;

    private static final int FINISH_SPLASH_ANIMATION_DURATION = 1000;
    // How long to wait after the animation has finished to notify the callback.
    private static final int FINISH_SPLASH_ANIMATION_POST_DELAY = 1500;

    private static final int DISPLAY_RIPPLE_ANIMATION_DURATION = 500;
    private static final int DISPLAY_RIPPLE_ANIMATION_POST_DELAY = 250;

    private static final int MAX_ZOOM = 13;

    private Resources resources;

    /**
     * Master multi media map manipulator monster.
     */
    private MapCrayons crayons;

    /**
     * User's current location.
     */
    private LatLng currentLocation;

    /**
     * Circle that represents the user's splash.
     */
    private Circle splashCircle;

    /**
     * Animator that pulsates the splash circle's radius.
     */
    private ValueAnimator splashRadiusAnimator;

    /**
     * Are we currently splashing?
     */
    private boolean splashing;

    /**
     * Initial map location.
     */
    private LatLng startingLocation = null;

    public MainWaveMapView(Resources resources) {
        this.resources = resources;
    }

    @Override
    public void setPresenter(WavePresenter presenter) {
        //this.presenter = presenter;
    }

    @Override
    public void setMapFragment(MapFragment mapFragment) {
        if (mapFragment.getMap() == null)
            throw new RuntimeException("Wait until the map is initialized to call this method.");
        crayons = new MapCrayons(mapFragment.getMap());
        zoomToStartingLocation();
    }

    @Override
    public void setStartingLocation(float lat, float lng) {
        startingLocation = new LatLng(lat, lng);
        if (!isDisplayingWave() && !isDrawingCurrentLocation()) {
            zoomToStartingLocation();
        }
    }

    private boolean isDisplayingWave() {
        return crayons != null && crayons.getNumCircles() > 0;
    }

    private boolean isDrawingCurrentLocation() {
        return crayons != null && crayons.isDrawingCurrentLocation();
    }

    private void zoomToStartingLocation() {
        if (crayons != null && startingLocation != null)
            crayons.zoomyZoom(startingLocation, MAX_ZOOM, false);
    }

    @Override
    public void setLocationInfo(LocationInfo locationInfo) {
        currentLocation = new LatLng(locationInfo.lastLat, locationInfo.lastLong);
        crayons.drawCurrentLocation(currentLocation);

        if (!splashing) {
            if (isDisplayingWave()) {
                crayons.zoomToFitCircles();
            } else {
                crayons.zoomToCurrentLocation(true);
            }
        } else {
            //TODO: ?
            showSplashing(true);
        }
    }

    @Override
    public void displayWave(Wave wave) {
        cancelSplashing();
        if (crayons != null) {
            crayons.clearCircles();

            if (wave != null) {
                drawRipples(wave.getRipples(), wave.getSplashId());
                crayons.zoomToFitCircles();
            } else {
                crayons.zoomToCurrentLocation(true);
            }
        }
    }

    @Override
    public void showSplashing(boolean splashing) {
        if (splashing)
            displaySplashing();
        else
            cancelSplashing();
    }

    private void displaySplashing() {
        if (crayons == null || splashing) return;

        splashing = true;
        crayons.clearCircles();
        if (currentLocation != null) {
            //TODO move these to class variables.
            int strokeColor = resources.getColor(R.color.map_view_splashing_stroke);
            int fillColor = resources.getColor(R.color.map_view_splashing_fill_begin);

            crayons.zoomToCurrentLocation(true);
            splashCircle = crayons.drawCircle(currentLocation, RIPPLE_RADIUS, fillColor, strokeColor);
            splashRadiusAnimator = getPulseRadiusAnimator();
            splashRadiusAnimator.addUpdateListener((animator) ->
                    splashCircle.setRadius((Integer) animator.getAnimatedValue()));
            splashRadiusAnimator.start();
        }
    }

    private ValueAnimator getPulseRadiusAnimator() {
        ValueAnimator animator = ValueAnimator.ofInt(RIPPLE_RADIUS + 100, RIPPLE_RADIUS)
                .setDuration(RADIUS_PULSE_ANIMATION_DURATION);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        return animator;
    }

    @Override
    public void animateSplash(final FinishedSplashingCallback callback) {
        if (splashRadiusAnimator != null) {
            splashRadiusAnimator.cancel();
            splashRadiusAnimator = null;
        }

        int startColor = resources.getColor(R.color.map_view_splashing_fill_begin);
        int endColor = resources.getColor(R.color.map_view_splashing_fill_end);

        ValueAnimator colorAnimator = createCircleColorAnimator(
                splashCircle, startColor, endColor, FINISH_SPLASH_ANIMATION_DURATION);

        colorAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                new Handler().postDelayed(() -> {
                    crayons.clearCircles();
                    if (callback != null) callback.onFinishSplashing();
                }, FINISH_SPLASH_ANIMATION_POST_DELAY);
            }
        });
        colorAnimator.start();
    }

    private void cancelSplashing() {
        if (splashing) {
            splashing = false;

            if (splashRadiusAnimator != null) {
                splashRadiusAnimator.cancel();
                splashRadiusAnimator = null;
            }
            crayons.clearCircles();
        }
    }

    @Override
    public void animateRipple(FinishedRipplingCallback callback) {
        if (crayons == null) {
            callback.onFinishRippling();
            return;
        }
        int startColor = resources.getColor(R.color.map_view_ripple_new_fill_begin);
        int endColor = resources.getColor(R.color.map_view_ripple_fill);
        int strokeColor = resources.getColor(R.color.map_view_ripple_new_stroke);

        Circle rippleCircle = crayons.drawCircle(currentLocation, RIPPLE_RADIUS, startColor, strokeColor);
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

    private static class MapCrayons {
        private GoogleMap map;
        private List<Circle> circles = new ArrayList<>();
        private Marker currentLocationMarker;

        public MapCrayons(@NonNull GoogleMap map) {
            this.map = map;
        }

        public int getNumCircles() {
            return circles.size();
        }

        public Circle drawCircle(LatLng center, int radius, int fillColor, int strokeColor) {
            Circle circle = map.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .fillColor(fillColor)
                    .strokeColor(strokeColor));
            circles.add(circle);
            return circle;
        }

        public void zoomToFitCircles() {
            LatLngBounds bounds = getCircleBounds();
            if (bounds != null)
                zoomToBounds(bounds);

        }

        private LatLngBounds getCircleBounds() {
            if (circles.size() == 0) return null;

            LatLng currentLocationLatLng = currentLocationMarker == null ? null : currentLocationMarker.getPosition();

            double minLat = currentLocationLatLng == null ? 999 : currentLocationLatLng.latitude - 0.025;
            double minLng = currentLocationLatLng == null ? 999 : currentLocationLatLng.longitude - 0.025;
            double maxLat = currentLocationLatLng == null ? -999 : currentLocationLatLng.latitude + 0.025;
            double maxLng = currentLocationLatLng == null ? -999 : currentLocationLatLng.longitude + 0.025;

            for (Circle circle : circles) {
                minLat = Math.min(minLat, circle.getCenter().latitude - 0.05);
                minLng = Math.min(minLng, circle.getCenter().longitude - 0.05);
                maxLat = Math.max(maxLat, circle.getCenter().latitude + 0.05);
                maxLng = Math.max(maxLng, circle.getCenter().longitude + 0.05);
            }

            return new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
        }

        private void zoomToBounds(LatLngBounds bounds) {
            final CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 0);
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

        public void clearCircles() {
            Iterator<Circle> iterator = circles.iterator();
            while (iterator.hasNext()) {
                iterator.next().remove();
                iterator.remove();
            }
        }

        public void zoomyZoom(LatLng center, int zoom, boolean animate) {
            if (animate)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));
            else
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));
        }

        public boolean isDrawingCurrentLocation() {
            return currentLocationMarker != null;
        }

        public Marker drawCurrentLocation(LatLng center) {
            if (isDrawingCurrentLocation()) {
                currentLocationMarker.setPosition(center);
            } else {
                initCurrentLocationMarker(center);
            }
            return null;
        }

        private void initCurrentLocationMarker(LatLng center) {
            BitmapDescriptor markerBitmap = BitmapDescriptorFactory.fromResource(R.drawable.current_location);
            currentLocationMarker = map.addMarker(new MarkerOptions()
                    .position(center)
                    .anchor(0.5f, 0.5f)
                    .icon(markerBitmap));
        }

        public void zoomToCurrentLocation(boolean animate) {
            if (currentLocationMarker != null)
                zoomyZoom(currentLocationMarker.getPosition(), MAX_ZOOM, animate);
        }
    }

    /**
     * Adds/Draws a list of LatLngs as ripples to the map.
     */
    private void drawRipples(List<Ripple> ripples, long splashId) {
        if (crayons == null) return;
        int fillColor = resources.getColor(R.color.map_view_ripple_fill);
        int strokeColor = resources.getColor(R.color.map_view_ripple_stroke);
        //TODO: splash
        for (Ripple ripple : ripples) {
            LatLng center = new LatLng(ripple.getLatitude(), ripple.getLongitude());
            crayons.drawCircle(center, RIPPLE_RADIUS, fillColor, strokeColor);
        }
    }

}
