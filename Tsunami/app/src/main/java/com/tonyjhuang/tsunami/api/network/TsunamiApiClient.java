package com.tonyjhuang.tsunami.api.network;

import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateRippleRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateUserRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.SplashRequest;

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
public class TsunamiApiClient {

    private final TsunamiService service;

    @Inject
    public TsunamiApiClient(TsunamiService service) {
        this.service = service;
    }

    public Observable<User> createUser(String guid) {
        CreateUserRequest request = new CreateUserRequest(guid);
        return service.createUser(request);
    }

    public Observable<Ripple> ripple(int waveId, String guid, double latitude, double longitude) {
        CreateRippleRequest request = new CreateRippleRequest(waveId, guid, latitude, longitude);
        return service.ripple(request);
    }

    public Observable<List<Wave>> getWaves(String guid, double latitude, double longitude) {
        return service.getWaves(guid, latitude, longitude);
    }

    public Observable<Wave> splash(double latitude, double longitude, String content, String guid) {
        SplashRequest request = new SplashRequest(latitude, longitude, content, guid);
        return service.splash(request);
    }
}
