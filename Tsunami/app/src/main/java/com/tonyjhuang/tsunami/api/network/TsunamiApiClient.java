package com.tonyjhuang.tsunami.api.network;

import android.app.Application;

import com.tonyjhuang.tsunami.api.dal.TsunamiCache;
import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.api.network.requestbodies.CommentRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateRippleRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateUserRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.DismissWaveRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.SplashRequest;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Wrapper for our network interface.
 * Lets us massage request data and/or use our own caches before hitting the network.
 * Also lets us manipulate return data before passing it to the caller.
 * Created by tonyjhuang on 8/17/14.
 */
@Singleton
public class TsunamiApiClient implements TsunamiApi {

    /**
     * Network connectivity.
     */
    private final TsunamiService service;
    /**
     * Current user's GUID
     */
    private final String userId;
    /**
     * Running on disk cache for everything.
     */
    private final TsunamiCache cache;

    @SuppressWarnings("unused")
    @Inject
    public TsunamiApiClient(Application application,
                            TsunamiService service,
                            TsunamiPreferences preferences,
                            TsunamiCache cache) {
        this.service = service;
        this.userId = preferences.id.get();
        this.cache = cache;
    }

    /* USERS USERS USERS USERS USERS USERS USERS USERS */

    public Observable<User> createUser() {
        CreateUserRequest request = new CreateUserRequest(userId);
        return service.createUser(request);
    }

    @Override
    public Observable<UserStats> getCurrentUserStats() {
        return getUserStats(this.userId);
    }

    @Override
    public Observable<UserStats> getUserStats(String userId) {
        return Observable.concat(
                checkCache(cache.get(userId, UserStats.class)),
                service.getUserStats(userId).map((userStat -> cache.put(userId, userStat))));
    }

    /* RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE */

    public Observable<Ripple> ripple(long waveId, double latitude, double longitude) {
        CreateRippleRequest request = new CreateRippleRequest(userId, waveId, latitude, longitude);
        return service.ripple(request);
    }

    /* OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN */

    public Observable<List<Wave>> getLocalWaves(double latitude, double longitude) {
        return service.getWaves(userId, latitude, longitude)
                .flatMap(Observable::from)
                .map((wave) -> cache.put(wave.getId(), wave))
                .toList();
    }

    @Override
    public Observable<Wave> getWave(long waveId) {
        return checkCache(cache.get(waveId, Wave.class));
    }

    @Override
    public Observable<Wave> splash(String title,
                                   String body,
                                   WaveContent.ContentType contentType,
                                   double latitude,
                                   double longitude) {
        SplashRequest request = new SplashRequest(userId, title, body, contentType, latitude, longitude);
        return service.splash(request);
    }

    @Override
    public Observable<Void> dismissWave(long waveId) {
        DismissWaveRequest request = new DismissWaveRequest(userId, waveId);
        return service.dismissWave(request);
    }

    @Override
    public Observable<Wave> comment(long waveId, String comment) {
        CommentRequest request = new CommentRequest(userId, waveId, comment);
        return service.comment(request).map((wave) -> cache.put(waveId, wave));
    }

    /* MISC MISC MISC MISC MISC MISC MISC MISC MISC MISC */

    private <T> Observable<T> checkCache(T cachedResult) {
        if (cachedResult == null)
            return Observable.empty();
        else
            return Observable.just(cachedResult);
    }
}
