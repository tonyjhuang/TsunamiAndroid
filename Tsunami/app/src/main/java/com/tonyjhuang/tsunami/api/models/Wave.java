package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Represents a single piece of content along with all of its reshares.
 * Created by tonyhuang on 8/5/14.
 */
public class Wave {
    @Expose
    private long waveId;
    @Expose
    private double minLat;
    @Expose
    private double minLong;
    @Expose
    private double maxLat;
    @Expose
    private double maxLong;
    @Expose
    private double splashId;
    @Expose
    private long lastUpdate;
    @Expose
    private long startTime;
    @Expose
    private int numRipples;
    @Expose
    private List<Ripple> ripples;

    public long getWaveId() {
        return waveId;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMinLong() {
        return minLong;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLong() {
        return maxLong;
    }

    public double getSplashId() {
        return splashId;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getNumRipples() {
        return numRipples;
    }

    public List<Ripple> getRipples() {
        return ripples;
    }
}
