package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.util.Collections;
import java.util.List;

/**
 * A single user.
 * Created by tonyhuang on 8/5/14.
 */
public class User extends ApiObject {
    @Expose
    private String name;
    @Expose
    private UserStats stats;
    @Expose
    private List<SocialProfile> socialProfiles;


    public String getName() {
        return name == null ? "Kevin" : name;
    }

    public UserStats getStats() {
        return stats;
    }

    @SuppressWarnings("unchecked")
    public List<SocialProfile> getSocialProfiles() {
        if (socialProfiles == null) return Collections.EMPTY_LIST;
        return socialProfiles;
    }

    /* Debugging */

    public static User createDebugUser(String name) {
        User user = new User();
        user.name = name == null ? "Anonymous" : name;
        return user;
    }
}
