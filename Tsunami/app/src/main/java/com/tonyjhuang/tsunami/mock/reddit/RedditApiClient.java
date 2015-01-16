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
import java.util.concurrent.TimeUnit;

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

    protected RedditApi service;

    @SuppressWarnings("unused")
    @Inject
    public RedditApiClient(Application application,
                           TsunamiService service,
                           TsunamiPreferences preferences,
                           TsunamiCache cache) {
        super(application, service, preferences, cache);
        this.service = build();
    }


    private String after;

    @Override
    public Observable<List<Wave>> getWaves(double latitude, double longitude) {
        return getTop("pics", 5)
                .flatMap(Observable::from)
                .filter(this::isValidRedditPost)
                .map(this::createWave)
                .toList()
                .delay(1, TimeUnit.SECONDS);
    }

    // Returns true only for self posts and i.imgur.com links!
    private boolean isValidRedditPost(RedditPost redditPost) {
        String domain = redditPost.domain;
        return redditPost.isSelf || domain.equals("i.imgur.com");
    }

    public Observable<List<RedditPost>> getTop(String subreddit, int limit) {
        return service.getTop(subreddit, limit, after)
                .doOnNext((response) -> this.after = response.data.after)
                .map((response) -> response.data.children)
                .flatMap(Observable::from)
                .map((data) -> data.post)
                .toList();
    }

    private Wave createWave(RedditPost post) {
        int numRipples = Math.max(post.ups, 1);
        return Wave.createDebugWave(post, generateRandomRipples(numRipples));
    }

    private RedditApi build() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(getGson()));

        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return builder.build().create(RedditApi.class);
    }

    private Gson getGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }
}
