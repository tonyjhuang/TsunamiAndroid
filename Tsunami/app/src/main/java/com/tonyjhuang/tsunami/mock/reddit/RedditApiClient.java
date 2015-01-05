package com.tonyjhuang.tsunami.mock.reddit;

import android.app.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tonyjhuang.tsunami.BuildConfig;
import com.tonyjhuang.tsunami.api.dal.TsunamiCache;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiService;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.mock.MockTsunamiApiClient;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.ArrayList;
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

    private RedditApi service;

    @SuppressWarnings("unused")
    @Inject
    public RedditApiClient(Application application,
                           TsunamiService service,
                           TsunamiPreferences preferences,
                           TsunamiCache cache) {
        super(application, service, preferences, cache);
        this.service = build();
    }

    @Override
    public Observable<List<Wave>> getWaves(double latitude, double longitude) {
        return getTop("showerthoughts").map((posts) -> {
            List<Wave> waves = new ArrayList<>();
            for (RedditPost post : posts) {
                waves.add(createWave(post));
            }
            return waves;
        });
    }

    public Observable<List<RedditPost>> getTop(String subreddit) {
        return service.getTop(subreddit).map((response) -> {
            List<RedditPost> posts = new ArrayList<>();
            for (RedditGetTopResponseDataChild data : response.data.children)
                posts.add(data.post);
            return posts;
        });
    }

    private Wave createWave(RedditPost post) {
        User author = User.createDebugUser(post.getAuthor());
        return Wave.createDebugWave(post.getTitle(), post.getSelftext(), generateRandomRipples(), author);
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
