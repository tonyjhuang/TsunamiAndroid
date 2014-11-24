package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

/**
 * The resharing of a wave.
 * Created by tonyhuang on 8/5/14.
 */
public class Ripple extends ApiObject {
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private double radius;
    @Expose
    private User user;
    @Expose
    private Wave wave;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRadius() {
        return radius;
    }

    public User getUser() {
        return user;
    }

    public Wave getWave() {
        return wave;
    }
}