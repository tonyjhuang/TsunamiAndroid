package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.util.Random;

/*
{
    "created_at": "2014-12-20T19:52:38Z",
    "guid": 451,
    "latitude": "42.3514",
    "longitude": "-71.0571",
    "radius": "0.025",
    "status": "active",
    "updated_at": "2014-12-20T19:52:38Z",
    "user_id": 111,
    "wave_id": 141
}
 */
public class Ripple extends ApiObject {
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private double radius;
    @Expose
    private long userId;
    @Expose
    private long waveId;
    @Expose
    private RippleStatus status;


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRadius() {
        return radius;
    }

    public long getUserId() {
        return userId;
    }

    public long getWaveId() {
        return waveId;
    }

    public RippleStatus getStatus() {
        return status;
    }

    public boolean isValidFor(double lat, double lon) {

        return squared(lat - latitude) + squared(lon - longitude) <= squared(radius);
    }

    private double squared(double n) {
        return n * n;
    }

    public static enum RippleStatus {
        ACTIVE, INACTIVE
    }

    /* DEBUG */

    public static Ripple createDebugRipple(double latitude, double longitude) {
        return new Ripple(latitude, longitude, 0.025);
    }

    private Ripple(double latitude, double longitude, double radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        setId((long) new Random().nextInt(9999));
    }

}