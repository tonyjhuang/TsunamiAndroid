package com.tonyjhuang.tsunami.mock.reddit;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

/**
 * Created by tony on 1/3/15.
 */
public class RedditPost {

    @Expose
    private String title;
    @Expose
    private String author;
    @Expose
    private String selftext;
    @Expose
    private boolean isSelf;

    public RedditPost(String title, String author, boolean isSelf) {
        this.title = title;
        this.author = author;
        this.isSelf = isSelf;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public String getSelftext() {
        return selftext;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
