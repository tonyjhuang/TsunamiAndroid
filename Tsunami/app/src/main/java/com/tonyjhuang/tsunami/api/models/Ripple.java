package com.tonyjhuang.tsunami.api.models;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tonyhuang on 8/5/14.
 */
public class Ripple extends TsunamiObject {
    @Expose
    @SerializedName("author_id")
    private long authorId;
    @Expose
    private Location location;
    @Expose
    private String comment;
    @Expose
    private int radius;

    public long getAuthorId() {
        return authorId;
    }

    public Location getLocation() {
        return location;
    }

    public String getComment() {
        return comment;
    }

    public int getRadius() {
        return radius;
    }
}
