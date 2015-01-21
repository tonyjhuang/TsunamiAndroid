package com.tonyjhuang.tsunami.mock.reddit;

import com.tonyjhuang.tsunami.api.models.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 1/20/15.
 */
public class RedditPostAndComments {

    private RedditPost post;
    private List<RedditPost> comments;

    public RedditPostAndComments(RedditPost post, List<RedditPost> comments) {
        this.post = post;
        this.comments = comments;
    }

    public RedditPost getPost() {
        return post;
    }

    public List<Comment> getComments() {
        List<Comment> tsunamiComments = new ArrayList<>();
        for (RedditPost post : comments) {
            tsunamiComments.add(Comment.createDebugComment(post));
        }
        return tsunamiComments;
    }

    public static RedditPostAndComments create(List<RedditGetResponse> responses) {
        RedditPost post = responses.get(0).getRedditPosts().get(0);

        List<RedditPost> comments = new ArrayList<>();
        for (RedditGetResponseDataChild dataChild : responses.get(1).data.children)
            comments.add(dataChild.post);

        return new RedditPostAndComments(post, comments);
    }
}
