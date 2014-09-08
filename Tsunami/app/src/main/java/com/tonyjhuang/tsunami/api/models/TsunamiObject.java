package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

/**
 * Created by tonyjhuang on 8/17/14.
 */
public class TsunamiObject {
    @Expose
    private long id;

    public long getId() {
        return id;
    }

    public boolean equals(Object object) {
        return (object instanceof TsunamiObject) && (id == ((TsunamiObject) object).getId());
    }

}
