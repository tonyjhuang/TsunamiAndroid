package com.tonyjhuang.tsunami.api.network;

import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.Wave;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Wrapper for our network interface.
 * Lets us massage request data and/or use our own caches before hitting the network.
 * Also lets us manipulate return data before passing it to the caller.
 * Created by tonyjhuang on 8/17/14.
 */
@Singleton
public class TsunamiApiClient {

    private TsunamiService service;

    @Inject
    public TsunamiApiClient(TsunamiService service) {
        this.service = service;
    }

    public Observable<User> getUser(long userId) {
        return service.getUser(userId);
    }

    public Observable<Integer> createUser(String userName) {
        return service.createUser(userName);
    }

    public Observable<List<Wave>> getAllWaves() {
        return service.getAllWaves();
    }

    public Observable<List<Wave>> getWaves(long userId, double lat, double lon) {
        return service.getWaves(userId, lat, lon);
    }

    public Observable<List<Ripple>> getRipples(long waveId) {
        return service.getRipples(waveId);
    }

    public Observable<Wave> addRipple(long userId, long waveId, double lat, double lon) {
        return service.addRipple(userId, waveId, lat, lon);
    }

    public Observable<Wave> splashText(long userId, String content, double lat, double lon) {
        return service.splashText(userId, content, lat, lon);
    }
}
