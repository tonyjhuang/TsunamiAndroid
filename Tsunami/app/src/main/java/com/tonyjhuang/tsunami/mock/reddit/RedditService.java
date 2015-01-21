package com.tonyjhuang.tsunami.mock.reddit;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by tony on 1/3/15.
 */
public interface RedditService {

    @GET("/r/{subreddit}/{what}.json")
    public Observable<RedditGetResponse> get(@Path("what") String what,
                                             @Path("subreddit") String subreddit,
                                             @Query("limit") int limit,
                                             @Query("after") String after);

    @GET("/r/{subreddit}/comments/{link_id}.json")
    public Observable<List<RedditGetResponse>> getComments(@Path("subreddit") String subreddit,
                                                             @Path("link_id") String linkId);
}
