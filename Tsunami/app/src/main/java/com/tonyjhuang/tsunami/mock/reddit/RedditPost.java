package com.tonyjhuang.tsunami.mock.reddit;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

/**
 * Created by tony on 1/3/15.
 */
public class RedditPost {

    @Expose
    public int ups;
    @Expose
    public long created;
    @Expose
    public String domain;
    @Expose
    public String title;
    @Expose
    public String author;
    @Expose
    public String selftext;
    @Expose
    public boolean isSelf;
    @Expose
    public int numComments;
    @Expose
    public String url;
    @Expose
    public String body;
    @Expose
    public String subreddit;
    @Expose
    public String id;

    public RedditPost(String title, String author, boolean isSelf) {
        this.title = title;
        this.author = author;
        this.isSelf = isSelf;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
