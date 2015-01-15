package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.mock.reddit.RedditPost;

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
    long splashId;
    @Expose
    int views;
    @Expose
    WaveContent content;
    @Expose
    List<Ripple> ripples;
    @Expose
    User user;
    @Expose
    int numComments;

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

    public int getNumComments() {
        return numComments;
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

    public static Wave createDebugWave(String title,
                                       String body,
                                       List<Ripple> ripples,
                                       User user,
                                       int numComments) {
        return new Wave(WaveContent.createDebugWaveContent(title, body), ripples, user, numComments);
    }

    public static Wave createDebugWave(RedditPost post, List<Ripple> ripples) {
        String title = post.title;
        String body = post.isSelf ? post.selftext : post.url;
        WaveContent.ContentType contentType = post.isSelf ? WaveContent.ContentType.TEXT : WaveContent.ContentType.IMAGE_LINK;
        User user = User.createDebugUser(post.author);
        Date createdAt = new Date(post.created * 1000);
        Timber.d("createdAt: " + createdAt + ", millis: " + (post.created * 1000));
        int views = post.ups;
        int numComments = post.numComments;

        Wave wave = new Wave();
        wave.content = WaveContent.createDebugWaveContent(title, body, contentType);
        wave.user = user;
        wave.setCreatedAt(createdAt);
        wave.views = views;
        wave.ripples = ripples;
        wave.numComments = numComments;
        wave.setId(Math.abs(new Random().nextLong()));

        return wave;
    }

    private Wave() {
    }

    private Wave(WaveContent content, List<Ripple> ripples, User user, int numComments) {
        this.content = content;
        this.ripples = ripples;
        this.splashId = ripples.get(0).getId();
        this.user = user;
        this.numComments = numComments;

        long earliestDate = 1420000000000l;
        long now = System.currentTimeMillis();
        long mod = now - earliestDate;
        Date rand = new Date((new Random().nextLong() % mod) + earliestDate);
        setCreatedAt(rand);
        setUpdatedAt(rand);

        setId(new Random().nextLong());
    }

    public void convertToImageLink(String imageUrl) {
        content.setBody(imageUrl);
        content.setContentType(WaveContent.ContentType.IMAGE_LINK);
    }
}
