package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

/**
 * A single user.
 * Created by tonyhuang on 8/5/14.
 */
public class User {
    @Expose
    private long userId;
    @Expose
    private String username;
    @Expose
    private long lastUpdate;
    @Expose
    private long startTime;

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getStartTime() {
        return startTime;
    }
}
