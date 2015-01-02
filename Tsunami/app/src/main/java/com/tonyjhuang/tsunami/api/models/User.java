package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.tonyjhuang.tsunami.mock.RandomString;

import java.util.Random;
import java.util.UUID;

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

    /* Debugging */
    public static User createDebugUser() {
        return new User();
    }

    private User() {
        this.guid = UUID.randomUUID().toString();
        this.name = (new Random().nextInt(2) == 0) ? "Anonymous" : new RandomString(10).nextString();
    }
}
