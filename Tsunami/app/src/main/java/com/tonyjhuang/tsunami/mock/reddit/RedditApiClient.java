package com.tonyjhuang.tsunami.mock.reddit;

import android.app.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tonyjhuang.tsunami.BuildConfig;
import com.tonyjhuang.tsunami.api.dal.TsunamiCache;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiService;
import com.tonyjhuang.tsunami.mock.MockTsunamiApiClient;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

/**
 * Created by tony on 1/3/15.
 */
@Singleton
public class RedditApiClient extends MockTsunamiApiClient {
    public static final String ENDPOINT = "https://www.reddit.com";

    protected final RedditService service;
    private final TsunamiCache cache;

    @SuppressWarnings("unused")
    @Inject
    public RedditApiClient(Application application,
                           TsunamiService service,
                           TsunamiPreferences preferences,
                           TsunamiCache cache) {
        super(application, service, preferences, cache);
        this.service = build();
        this.cache = cache;
    }

    // used to page through reddit pages.
    private String after;

    @Override
    public Observable<List<Wave>> getLocalWaves(double latitude, double longitude) {
        return get("new", "pics+showerthoughts", 15)
                .flatMap(Observable::from)
                .filter(this::isValidRedditPost)
                .map(this::createWave)
                .map((wave) -> cache.put(wave.getId(), wave))
                .toList();
    }

    // Returns true only for self posts and i.imgur.com links!
    private boolean isValidRedditPost(RedditPostAndComments redditPostAndComments) {
        RedditPost redditPost = redditPostAndComments.getPost();
        String domain = redditPost.domain;
        return redditPost.isSelf || domain.equals("i.imgur.com");
    }

    public Observable<List<RedditPostAndComments>> get(String what, String subreddit, int limit) {
        return service.get(what, subreddit, limit, after)
                .doOnNext((response) -> this.after = response.data.after)
                .map(RedditGetResponse::getRedditPosts)
                .flatMap(Observable::from)
                .concatMap(this::getComments)
                .toList();
    }

    public Observable<RedditPostAndComments> getComments(RedditPost post) {
        return service.getComments(post.subreddit, post.id)
                .map(RedditPostAndComments::create);
    }

    @Override
    public Observable<Wave> getWave(long waveId) {
        return Observable.just(cache.get(waveId, Wave.class));
    }

    private Wave createWave(RedditPostAndComments postAndComments) {
        return Wave.createDebugWave(postAndComments.getPost(),
                postAndComments.getComments(),
                generateRandomRipples(postAndComments.getPost().ups));
    }

    private RedditService build() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(getGson()));

        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return builder.build().create(RedditService.class);
    }

    private Gson getGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private <T> Observable<T> checkCache(T cachedResult) {
        if (cachedResult == null)
            return Observable.empty();
        else
            return Observable.just(cachedResult);
    }
}
