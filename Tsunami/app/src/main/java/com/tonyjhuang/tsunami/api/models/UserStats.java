package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.util.Random;

/*
{
  "splashes": 2,
  "ripples": 3,
  "views_across_waves": 94,
  "ripples_across_waves": 3
}
 */
public class UserStats {
    @Expose
    private int splashes;
    @Expose
    private int ripples;
    @Expose
    private long viewsAcrossWaves;
    @Expose
    private long ripplesAcrossWaves;

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

    /* Debugging */
    public static UserStats createDebugUserStats() {
        Random random = new Random();
        return new UserStats(random.nextInt(), random.nextInt(), random.nextLong(), random.nextLong());
    }

    private UserStats(int splashes, int ripples, long viewsAcrossWaves, long ripplesAcrossWaves) {
        this.splashes = Math.abs(splashes % 30);
        this.ripples = Math.abs(ripples % 3000);
        this.viewsAcrossWaves = Math.abs(viewsAcrossWaves % 30000);
        this.ripplesAcrossWaves = Math.abs(ripplesAcrossWaves % 3000);
    }
}
