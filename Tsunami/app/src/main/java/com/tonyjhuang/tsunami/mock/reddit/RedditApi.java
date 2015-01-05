package com.tonyjhuang.tsunami.mock.reddit;

import com.tonyjhuang.tsunami.api.models.User;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by tony on 1/3/15.
 */
public interface RedditApi {

    @GET("/r/{subreddit}/top.json")
    public Observable<RedditGetTopResponse> getTop(@Path("subreddit") String subreddit);

}
