package com.tonyjhuang.tsunami.mock.reddit;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 1/3/15.
 */
public class RedditGetResponse {
    @Expose
    RedditGetResponseData data;

    public List<RedditPost> getRedditPosts() {
        List<RedditPost> redditPosts = new ArrayList<>();
        for(RedditGetResponseDataChild dataChild : data.children)
            redditPosts.add(dataChild.post);
        return redditPosts;
    }
}
