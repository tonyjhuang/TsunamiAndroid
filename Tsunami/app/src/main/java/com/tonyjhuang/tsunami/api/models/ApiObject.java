package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by tony on 11/23/14.
 */
public class ApiObject implements Serializable{
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
}
