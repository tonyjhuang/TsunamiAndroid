package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.tonyjhuang.tsunami.mock.reddit.RedditPost;

import java.util.Date;
import java.util.Random;

/*
{
  "guid": "12345",
  "wave_id": 1,
  "body": "comment"
}
 */
public class Comment extends ApiObject{
    @Expose
    private User author;
    @Expose
    private long waveId;
    @Expose
    private String body;

    public User getAuthor() {
        return author;
    }

    public long getWaveId() {
        return waveId;
    }

    public String getBody() {
        return body;
    }

    public static Comment createDebugComment(RedditPost redditPost) {
        return createDebugComment(redditPost.author, redditPost.body, new Date(redditPost.created * 1000));
    }

    public static Comment createDebugComment(String author, String body) {
        return createDebugComment(author, body, new Date());
    }

    public static Comment createDebugComment(String author, String body, Date timestamp) {
        Comment comment = new Comment();
        comment.author = User.createDebugUser(author);
        comment.waveId = Math.abs(new Random().nextLong());
        comment.body = body;
        comment.setCreatedAt(timestamp);
        comment.setUpdatedAt(timestamp);
        return comment;
    }
}
