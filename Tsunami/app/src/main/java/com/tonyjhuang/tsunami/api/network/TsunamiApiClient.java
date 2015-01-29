package com.tonyjhuang.tsunami.api.network;

import android.app.Application;

import com.tonyjhuang.tsunami.api.dal.TsunamiCache;
import com.tonyjhuang.tsunami.api.models.ApiObject;
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
import com.tonyjhuang.tsunami.logging.Timber;
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
     * App prefs
     */
    private final TsunamiPreferences prefs;
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
        this.prefs = preferences;
        this.cache = cache;

    }

    /* USERS USERS USERS USERS USERS USERS USERS USERS */

    @Override
    public boolean isLoggedIn() {
        return prefs.userId.get() != TsunamiPreferences.NO_USER_ID;
    }

    @Override
    public Observable<User> createUser() {
        CreateUserRequest request = new CreateUserRequest(prefs.guid.get());
        return service.createUser(request).doOnNext((user) -> prefs.userId.set(user.getId()));
    }

    @Override
    public Observable<UserStats> getCurrentUserStats() {
        return getUserId().flatMap(this::getUserStats);
    }

    @Override
    public Observable<UserStats> getUserStats(long userId) {
        return Observable.concat(
                checkCache(cache.get(userId, User.class)).map(User::getStats),
                service.getUser(userId).doOnNext(this::cacheObject).map(User::getStats));
    }

    @Override
    public Observable<List<Wave>> getCurrentUserWaves() {
        return getUserId().flatMap(this::getUserWaves);
    }

    public Observable<List<Wave>> getUserWaves(long userId) {
        return service.getUserWaves(userId)
                .doOnNext(this::cacheObjects);
    }

    /* OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN */

    public Observable<List<Wave>> getLocalWaves(double latitude, double longitude) {
        return getUserId()
                .flatMap((userId) -> service.getWaves(userId, latitude, longitude))
                .doOnNext(this::cacheObjects);
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
        return getUserId().flatMap((userId) -> {
            SplashRequest request = new SplashRequest(userId, title, body, contentType, latitude, longitude);
            return service.splash(request);
        });
    }

    /* WAVE INTERACTION WAVE INTERACTION WAVE INTERACTION WAVE INTERACTION */

    @Override
    public Observable<Void> dismissWave(long waveId) {
        return getUserId().flatMap((userId) -> {
            DismissWaveRequest request = new DismissWaveRequest(userId, waveId);
            return service.dismissWave(request);
        });
    }

    public Observable<Ripple> ripple(long waveId, double latitude, double longitude) {
        return getUserId().flatMap((userId) -> {
            CreateRippleRequest request = new CreateRippleRequest(userId, waveId, latitude, longitude);
            return service.ripple(request);
        });
    }

    @Override
    public Observable<Wave> comment(long waveId, String comment) {
        return getUserId().flatMap((userId) -> {
            CommentRequest request = new CommentRequest(userId, waveId, comment);
            return service.comment(request);
        }).doOnNext(this::cacheObject);
    }

    /* MISC MISC MISC MISC MISC MISC MISC MISC MISC MISC */

    private Observable<Long> getUserId() {
        Timber.d("isLoggedIn: " + isLoggedIn());
        return isLoggedIn() ? Observable.just(prefs.userId.get()) : createUser().map(User::getId);
    }

    private <T> Observable<T> checkCache(T cachedResult) {
        if (cachedResult == null)
            return Observable.empty();
        else
            return Observable.just(cachedResult);
    }

    private <T extends ApiObject> void cacheObjects(List<T> apiObjects) {
        for (T apiObject : apiObjects)
            cache.put(apiObject.getId(), apiObject);
    }

    private <T extends ApiObject> void cacheObject(T apiObject) {
        cache.put(apiObject.getId(), apiObject);
    }
}
