package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

/**
 * The resharing of a wave.
 * Created by tonyhuang on 8/5/14.
 */
public class Ripple {
    @Expose
    private long rippleId;
    @Expose
    private long waveId;
    @Expose
    private long userId;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private long timestamp;

    public long getRippleId() {
        return rippleId;
    }

    public long getWaveId() {
        return waveId;
    }

    public long getUserId() {
        return userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }
}