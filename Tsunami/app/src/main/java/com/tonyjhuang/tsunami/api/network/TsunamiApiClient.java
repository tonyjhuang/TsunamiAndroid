package com.tonyjhuang.tsunami.api.network;

import android.app.Application;
import android.util.LruCache;

import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateRippleRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateUserRequest;
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

    private final TsunamiService service;
    private final String userId;

    @SuppressWarnings("unused")
    @Inject
    public TsunamiApiClient(Application application, TsunamiService service, TsunamiPreferences preferences) {
        this.service = service;
        this.userId = preferences.id.get();
    }

    public Observable<User> createUser() {
        CreateUserRequest request = new CreateUserRequest(userId);
        return service.createUser(request);
    }

    @Override
    public Observable<UserStats> getCurrentUserStats() {
        return getUserStats(this.userId);
    }

    private LruCache<String, UserStats> getUserStatsCache = new LruCache<>(64);
    @Override
    public Observable<UserStats> getUserStats(String userId) {
        return Observable.concat(
                getCacheObservable(getUserStatsCache.get(userId)),
                service.getUserStats(userId).map((userStat -> getUserStatsCache.put(userId, userStat))));
    }

    public Observable<Ripple> ripple(long waveId, double latitude, double longitude) {
        CreateRippleRequest request = new CreateRippleRequest(userId, waveId, latitude, longitude);
        return service.ripple(request);
    }

    public Observable<List<Wave>> getWaves(double latitude, double longitude) {
        return service.getWaves(userId, latitude, longitude);
    }

    public Observable<Wave> splash(String title, String body, double latitude, double longitude) {
        SplashRequest request = new SplashRequest(userId, title, body, latitude, longitude);
        return service.splash(request);
    }

    private <T> Observable<T> getCacheObservable(T cachedResult) {
        if (cachedResult == null)
            return Observable.empty();
        else
            return Observable.just(cachedResult);
    }
}
