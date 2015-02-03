package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.util.UUID;

/**
 * A single user.
 * Created by tonyhuang on 8/5/14.
 */
public class User extends ApiObject {
    @Expose
    private String name;
    @Expose
    private UserStats stats;

    public String getName() {
        return name == null ? "Kevin" : name;
    }

    public UserStats getStats() {
        return stats;
    }

    /* Debugging */

    public static User createDebugUser(String name) {
        User user = new User();
        user.name = name == null ? "Anonymous" : name;
        return user;
    }
}
