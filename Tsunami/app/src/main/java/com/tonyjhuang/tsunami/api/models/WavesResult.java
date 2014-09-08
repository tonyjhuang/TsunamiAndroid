package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Created by tonyjhuang on 8/17/14.
 */
public class WavesResult {
    @Expose
    @SerializedName("items")
    private List<Wave> waves;

    public List<Wave> getWaves() {
        if(waves != null) {
            return waves;
        }
        return Collections.EMPTY_LIST;
    }
}
