package com.tonyjhuang.tsunami.location;
import android.location.Location;

/**
 * Override this class and pass to LocationManager to handle callbacks with location.
 */
public abstract class LocationResult {
    public abstract void gotLocation(Location location);
}