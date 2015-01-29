package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
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
public class UserStats implements Serializable {
    @Expose
    private long splashes;
    @Expose
    private long viewsAcrossWaves;
    @Expose
    private long ripplesAcrossWaves;
    @Expose
    private long viewed;
    @Expose
    private long ripples;
    @Expose
    private double rippleChance;


    public long getSplashes() {
        return splashes;
    }

    public long getRipples() {
        return ripples;
    }

    public long getViewsAcrossWaves() {
        return viewsAcrossWaves;
    }

    public long getRipplesAcrossWaves() {
        return ripplesAcrossWaves;
    }

    public long getViewed() {
        return viewed;
    }

    public int getRippleChance() {
        return (int) (rippleChance * 100);
    }

    /* Debugging */
    public static UserStats createDebugUserStats() {
        Random random = new Random();
        UserStats stats = new UserStats();

        stats.splashes = Math.abs(random.nextInt() % 30);
        stats.viewsAcrossWaves = Math.abs(random.nextInt() % 30000);
        stats.ripplesAcrossWaves = Math.abs(random.nextLong() % 3000);
        stats.viewed = Math.max(stats.viewsAcrossWaves, stats.ripplesAcrossWaves);
        stats.ripples = Math.min(stats.viewsAcrossWaves, stats.ripplesAcrossWaves);
        stats.rippleChance = (double) stats.ripplesAcrossWaves / stats.viewsAcrossWaves;

        return stats;
    }
}
