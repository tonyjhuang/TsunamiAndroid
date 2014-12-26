package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    private long viewsAcrossWaves;
    @Expose
    private long ripplesAcrossWaves;
    @Expose
    private int views;
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

    public int getViews() {
        return views;
    }

    public double getRippleChance() {
        return rippleChance;
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

    private UserStats(int splashes, long viewsAcrossWaves, long ripplesAcrossWaves, int views, int ripples) {
        this.splashes = splashes;
        this.viewsAcrossWaves = viewsAcrossWaves;
        this.ripplesAcrossWaves = ripplesAcrossWaves;
        this.views = Math.max(views, ripples);
        this.ripples = Math.min(views, ripples);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        this.rippleChance = Double.valueOf(df.format(((float) ripples) / views * 100));
    }
}
