package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

/**
 * Created by tonyhuang on 8/5/14.
 */
public class User extends TsunamiObject {
    @Expose
    private String name;

    public String getName() {
        return name;
    }
}
