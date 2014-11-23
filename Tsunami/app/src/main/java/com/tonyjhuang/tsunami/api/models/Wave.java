package com.tonyjhuang.tsunami.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Represents a single piece of content along with all of its reshares.
 * Created by tonyhuang on 8/5/14.
 */
public class Wave implements Parcelable {
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


    private String debugTitle;

    private String debugText;

    public static Wave createDebugWave(String title, String text) {
        return new Wave(title, text);
    }

    private Wave(String debugTitle, String debugText) {
        this.debugTitle = debugTitle;
        this.debugText = debugText;
    }

    public String getDebugTitle() {
        return debugTitle;
    }

    public String getDebugText() {
        return debugText;
    }

    /* Parcelable interface */

    public static final Parcelable.Creator<Wave> CREATOR = new Parcelable.Creator<Wave>() {
        public Wave createFromParcel(Parcel in) {
            return new Wave(in);
        }

        @Override
        public Wave[] newArray(int size) {
            return new Wave[size];
        }
    };

    private Wave(Parcel in) {
        debugText = in.readString();
        debugText = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(debugTitle);
        out.writeString(debugText);
    }
}
