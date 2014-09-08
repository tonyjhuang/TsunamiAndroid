package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tonyhuang on 8/5/14.
 */
public class Wave extends TsunamiObject{
    @Expose
    @SerializedName("author_id")
    private long authorId;
    @Expose
    @SerializedName("content_type")
    private WaveContentType contentType;
    @Expose
    @SerializedName("message")
    private String title;
    @Expose
    private List<Long> nodes;

    public long getAuthorId() {
        return authorId;
    }

    public WaveContentType getContentType() {
        return contentType;
    }

    public String getTitle() {
        return title;
    }

    public List<Long> getNodes() {
        return nodes;
    }

    public static enum WaveContentType{
        PICTURE,
        VIDEO,
        TEXT,
        AUDIO
    }
}
