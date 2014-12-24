package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.RowId;
import java.util.List;

/*
{
    "id": 141,
    "origin_ripple_id": 451,
    "views": 3,
    "content": {
        "body": null,
        "id": 111,
        "title": null,
        "wave_id": 141
    },
    "ripples": [
        {
            "created_at": "2014-12-20T19:52:38Z",
            "id": 451,
            "latitude": "42.3514",
            "longitude": "-71.0571",
            "radius": "0.025",
            "status": "active",
            "updated_at": "2014-12-20T19:52:38Z",
            "user_id": 111,
            "wave_id": 141
        }
    ],
    "user": null
}
 */
public class Wave{

    @Expose
    private long id;
    /**
     * Id of the Splash for this Wave (the original Ripple).
     */
    @Expose
    @SerializedName("origin_ripple_id")
    private int splashId;
    @Expose
    private int views;
    @Expose
    private WaveContent content;
    @Expose
    private List<Ripple> ripples;
    @Expose
    private User user;

    public static Wave createDebugWave(String title, String body, List<Ripple> ripples) {
        return new Wave(WaveContent.createDebugWaveContent(title, body), ripples);
    }

    private Wave(WaveContent content, List<Ripple> ripples) {
        this.content = content;
        this.ripples = ripples;
    }

    public long getId() {
        return id;
    }

    public int getSplashId() {
        return splashId;
    }

    public int getViews() {
        return views;
    }

    public WaveContent getContent() {
        return content;
    }

    public List<Ripple> getRipples() {
        return ripples;
    }

    public User getUser() {
        return user;
    }

    /**
     * Do the passed in coordinates correspond with at least one ripple within this wave?
     */
    public boolean isValidFor(double lat, double lon) {
        for(Ripple ripple : ripples) {
            if(ripple.isValidFor(lat, lon))
                return true;
        }
        return false;
    }
}
