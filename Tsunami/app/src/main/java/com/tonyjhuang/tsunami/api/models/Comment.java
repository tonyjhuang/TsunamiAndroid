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
    private User user;
    @Expose
    private long waveId;
    @Expose
    private String body;

    public User getUser() {
        return user;
    }

    public long getWaveId() {
        return waveId;
    }

    public String getBody() {
        return body;
    }

    public static Comment createLocalComment(User author, String body) {
        Date now = new Date();

        Comment comment = new Comment();
        comment.user = author;
        comment.body = body;
        comment.setUpdatedAt(now);
        comment.setCreatedAt(now);

        return comment;
    }

    public static Comment createDebugComment(RedditPost redditPost) {
        return createDebugComment(redditPost.author, redditPost.body, new Date(redditPost.created * 1000));
    }

    public static Comment createDebugComment(String author, String body) {
        return createDebugComment(author, body, new Date());
    }

    public static Comment createDebugComment(String author, String body, Date timestamp) {
        Comment comment = new Comment();
        comment.user = User.createDebugUser(author);
        comment.waveId = Math.abs(new Random().nextLong());
        comment.body = body;
        comment.setCreatedAt(timestamp);
        comment.setUpdatedAt(timestamp);
        return comment;
    }
}
