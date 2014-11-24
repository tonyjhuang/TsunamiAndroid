package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents a single piece of content along with all of its reshares.
 * Created by tonyhuang on 8/5/14.
 */
public class Wave extends ApiObject {

    /**
     * Id of the Splash for this Wave (the original Ripple).
     */
    @Expose
    @SerializedName("origin_ripple_id")
    private int splashId;
    @Expose
    String content;
    @Expose
    List<Ripple> ripples;
    @Expose
    User user;

    private String debugTitle, debugText;

    public static Wave createDebugWave(String title, String text) {
        return new Wave(title, text);
    }

    private Wave(String debugTitle, String debugText) {
        this.debugTitle = debugTitle;
        this.debugText = debugText;
    }

    public String getDebugTitle() {
        return debugTitle;
    }

    public String getDebugText() {
        return debugText;
    }
}
