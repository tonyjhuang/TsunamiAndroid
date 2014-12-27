package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

/*
{
    "viewed": 2,
    "ripples": 0,
    "splashes": 0,
    "ripple_chance": 0,
    "views_across_waves": 0,
    "ripples_across_waves": 0
}
*/
public class UserStats extends ApiObject implements Serializable {
    @Expose
    private int splashes;
    @Expose
    private long viewsAcrossWaves;
    @Expose
    private long ripplesAcrossWaves;
    @Expose
    private int viewed;
    @Expose
    private int ripples;
    @Expose
    private double rippleChance;


    public int getSplashes() {
        return splashes;
    }

    public int getRipples() {
        return ripples;
    }

    public long getViewsAcrossWaves() {
        return viewsAcrossWaves;
    }

    public long getRipplesAcrossWaves() {
        return ripplesAcrossWaves;
    }

    public int getViewed() {
        return viewed;
    }

    public int getRippleChance() {
        return (int) (rippleChance * 100);
    }

    /* Debugging */
    public static UserStats createDebugUserStats() {
        Random random = new Random();
        return new UserStats(Math.abs(random.nextInt() % 30),
                Math.abs(random.nextInt() % 30000),
                Math.abs(random.nextLong() % 3000),
                Math.abs(random.nextInt() % 30000),
                Math.abs(random.nextInt() % 3000));
    }

    private UserStats(int splashes, long viewsAcrossWaves, long ripplesAcrossWaves, int viewed, int ripples) {
        this.splashes = splashes;
        this.viewsAcrossWaves = viewsAcrossWaves;
        this.ripplesAcrossWaves = ripplesAcrossWaves;
        this.viewed = Math.max(viewed, ripples);
        this.ripples = Math.min(viewed, ripples);

        this.rippleChance = (double) ripples / viewed;
    }
}
