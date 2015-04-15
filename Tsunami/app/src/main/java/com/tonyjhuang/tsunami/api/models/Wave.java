package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tonyjhuang.tsunami.mock.reddit.RedditPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/*
{
    "id": 1,
    "created_at": "2015-04-14T01:31:36Z",
    "updated_at": "2015-04-14T01:31:36Z",
    "origin_ripple_id": 1,
    "views": 0,
    "content": {
        "id": 1,
        "type": "TextContent",
        "caption": "sux"
    },
    "ripples": [
        {
        "created_at": "2015-04-14T01:31:36Z",
        "id": 1,
        "latitude": "42.3405",
        "longitude": "-71.1041",
        "radius": "0.025",
        "updated_at": "2015-04-14T01:31:36Z",
        "user_id": 2,
        "wave_id": 1
        }
    ],
    "comments": [],
    "user": {
        "id": 2,
        "created_at": "2015-04-14T01:22:28Z",
        "updated_at": "2015-04-14T01:22:28Z",
        "viewed": 0,
        "social_profiles": []
    }
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
    @Expose
    private List<Comment> comments;

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

    public Ripple getSplash() {
        if (ripples == null || ripples.size() == 0)
            return null;
        return ripples.get(0);
    }

    public User getUser() {
        return user;
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getComments() {
        if (comments == null) return Collections.EMPTY_LIST;
        return comments;
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

    public Wave addComment(Comment comment) {
        if (comments == null) comments = new ArrayList<Comment>();
        comments.add(comment);
        return this;
    }

    @Override
    public boolean equals(Object otherWave) {
        return otherWave instanceof Wave && getId() == ((Wave) otherWave).getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() % Integer.MAX_VALUE);
    }


    public static Wave createLocalWave(String caption) {
        Date now = new Date();
        Wave wave = new Wave();
        wave.content = WaveContent.createLocalWaveContent(caption);
        wave.user = User.createLocalUser();
        wave.setCreatedAt(now);
        wave.setUpdatedAt(now);
        return wave;
    }

    /* Debugging */

    public static Wave createDebugWave(String title,
                                       String body,
                                       List<Ripple> ripples,
                                       User user,
                                       List<Comment> comments) {
        WaveContent content = WaveContent.createDebugWaveContent(title, body);
        long earliestDate = 1420000000000l;
        long now = System.currentTimeMillis();
        long mod = now - earliestDate;
        Date rand = new Date((new Random().nextLong() % mod) + earliestDate);

        Wave wave = new Wave();
        wave.content = content;
        wave.ripples = ripples;
        wave.user = user;
        wave.comments = comments;
        wave.setCreatedAt(rand);
        wave.setUpdatedAt(rand);
        wave.setId(new Random().nextLong());

        return wave;
    }

    public static Wave createDebugWave(RedditPost post, List<Comment> comments, List<Ripple> ripples) {
        String title = post.title;
        String body = post.isSelf ? post.selftext : post.url;
        WaveContent.ContentType contentType = post.isSelf ? WaveContent.ContentType.text_content : WaveContent.ContentType.image_link;
        User user = User.createDebugUser(post.author);
        Date createdAt = new Date(post.created * 1000);
        int views = post.ups * 10;

        Wave wave = new Wave();
        wave.content = WaveContent.createDebugWaveContent(title, body, contentType);
        wave.user = user;
        wave.setCreatedAt(createdAt);
        wave.views = views;
        wave.ripples = ripples;
        wave.comments = comments;
        wave.setId(Math.abs(new Random().nextLong()));

        return wave;
    }
}
