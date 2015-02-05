package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

/**
 * A user's social handle for a given social service.
 * Created by tony on 2/3/15.
 */
public class SocialProfile extends ApiObject {
    @Expose
    private String username;
    @Expose
    private Service service;

    public String getUsername() {
        return username;
    }

    public Service getService() {
        return service;
    }

    public static enum Service {
        tsunami, twitter, facebook, google_plus, soundcloud, youtube, deviant_art, instagram
    }

    /* debug */
    public static SocialProfile createDebugSocialProfile(String name) {
        SocialProfile s = new SocialProfile();
        s.username = name;
        s.service = Service.tsunami;
        return s;
    }
}
