package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Created by tonyjhuang on 8/19/14.
 */
public class RipplesResult {
    @Expose
    @SerializedName("items")
    private List<Ripple> ripples;

    public List<Ripple> getRipples() {
        if(ripples != null) {
            return ripples;
        }
        return Collections.EMPTY_LIST;
    }
}
