package com.tonyjhuang.tsunami.api.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.tonyjhuang.tsunami.api.parsers.TsunamiGson;

import java.io.Serializable;
import java.util.Date;

public abstract class ApiObject implements Serializable {
    private static Gson gson = TsunamiGson.buildGson();

    @Expose
    private long id;
    @Expose
    private Date createdAt;
    @Expose
    private Date updatedAt;

    public long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    /* For debugging */
    protected void setId(long id) {
        this.id = id;
    }

    protected void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    protected void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
