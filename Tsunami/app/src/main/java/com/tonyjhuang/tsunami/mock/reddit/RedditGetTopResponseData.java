package com.tonyjhuang.tsunami.mock.reddit;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by tony on 1/3/15.
 */
public class RedditGetTopResponseData {
    @Expose
    List<RedditGetTopResponseDataChild> children;
}
