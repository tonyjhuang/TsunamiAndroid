package com.tonyjhuang.tsunami.mock.reddit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tony on 1/3/15.
 */
public class RedditGetTopResponseDataChild {
    @Expose
    @SerializedName("data")
    RedditPost post;
}
