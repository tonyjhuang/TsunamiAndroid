package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

/**
 * A single user.
 * Created by tonyhuang on 8/5/14.
 */
public class User extends ApiObject {
    /**
     * Device GUID
     */
    @Expose
    private String guid;
    @Expose
    private String name;

    public String getGuid() {
        return guid;
    }

    public String getName() {
        return name == null ? "Anonymous" : name;
    }
}
