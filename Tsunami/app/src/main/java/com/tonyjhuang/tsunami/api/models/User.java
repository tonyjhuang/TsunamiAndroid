package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.tonyjhuang.tsunami.logging.Timber;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A single user.
 * Created by tonyhuang on 8/5/14.
 */
public class User extends ApiObject {
    @Expose
    private UserStats stats;
    @Expose
    private List<SocialProfile> socialProfiles;

    public String getName() {
        if (socialProfiles == null || socialProfiles.size() != 1)
            return "Anonymous";
        return socialProfiles.get(0).getUsername();
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
        SocialProfile socialProfile = SocialProfile.createDebugSocialProfile(name);
        user.socialProfiles = Arrays.asList(socialProfile);
        return user;
    }
}
