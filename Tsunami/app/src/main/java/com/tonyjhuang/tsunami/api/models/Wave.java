package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Random;

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
public class Wave extends ApiObject {

    /**
     * Id of the Splash for this Wave (the original Ripple).
     */
    @Expose
    @SerializedName("origin_ripple_id")
    private long splashId;
    @Expose
    private int views;
    @Expose
    private WaveContent content;
    @Expose
    private List<Ripple> ripples;
    @Expose
    private User user;

    public long getSplashId() {
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
        for (Ripple ripple : ripples) {
            if (ripple.isValidFor(lat, lon))
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object otherWave) {
        return otherWave instanceof Wave && getId() == ((Wave) otherWave).getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() % Integer.MAX_VALUE);
    }

    /* Debugging */

    public static Wave createDebugWave(String title, String body, List<Ripple> ripples, User user) {
        return new Wave(WaveContent.createDebugWaveContent(title, body), ripples, user);
    }

    private Wave(WaveContent content, List<Ripple> ripples, User user) {
        this.content = content;
        this.ripples = ripples;
        this.splashId = ripples.get(0).getId();
        this.user = user;

        long earliestDate = 1420000000000l;
        long now = System.currentTimeMillis();
        long mod = now - earliestDate;
        Date rand = new Date((new Random().nextLong() % mod) + earliestDate);
        setCreatedAt(rand);
        setUpdatedAt(rand);

        setId(new Random().nextLong());
    }
}
