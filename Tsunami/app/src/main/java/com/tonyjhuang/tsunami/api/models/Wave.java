package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyhuang on 8/5/14.
 */
public class Wave extends TsunamiObject {
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
    private List<Ripple> ripples;
    @Expose
    private String message;

    public Wave(long authorId,
                WaveContentType contentType,
                String title,
                List<Ripple> ripples,
                String message) {
        this.authorId = authorId;
        this.contentType = contentType;
        this.title = title;
        this.ripples = ripples;
        this.message = message;
    }

    public long getAuthorId() {
        return authorId;
    }

    public WaveContentType getContentType() {
        return contentType;
    }

    public String getTitle() {
        return title;
    }

    public List<Ripple> getRipples() {
        return ripples;
    }

    public String getMessage() {
        return message;
    }

    public static enum WaveContentType {
        PICTURE,
        VIDEO,
        TEXT,
        AUDIO
    }

    public static class WaveBuilder {
        private long authorId;
        private WaveContentType contentType;
        private String title;
        private List<Ripple> ripples = new ArrayList<Ripple>();
        private String message;

        public static WaveBuilder getInstance() {
            return new WaveBuilder();
        }

        public WaveBuilder authorId(long authorId) {
            this.authorId = authorId;
            return this;
        }

        public WaveBuilder contentType(WaveContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public WaveBuilder title(String title) {
            this.title = title;
            return this;
        }

        public WaveBuilder ripples(List<Ripple> ripples) {
            this.ripples = ripples;
            return this;
        }

        public WaveBuilder message(String message) {
            this.message = message;
            return this;
        }

        public Wave build() {
            return new Wave(authorId, contentType, title, ripples, message);
        }
    }
}
