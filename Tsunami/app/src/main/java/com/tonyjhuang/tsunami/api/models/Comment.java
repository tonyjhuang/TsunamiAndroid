package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.tonyjhuang.tsunami.mock.reddit.RedditPost;

import java.util.Random;

/*
{
  "guid": "12345",
  "wave_id": 1,
  "body": "comment"
}
 */
public class Comment {
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
        return createDebugComment(redditPost.author, redditPost.body);
    }

    public static Comment createDebugComment(String author, String body) {
        Comment comment = new Comment();
        comment.author = User.createDebugUser(author);
        comment.waveId = Math.abs(new Random().nextLong());
        comment.body = body;
        return comment;
    }
}
