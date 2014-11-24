package com.tonyjhuang.tsunami.api.network;

import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateRippleRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateUserRequest;
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
public class TsunamiApiClient {

    private final TsunamiService service;
    private final TsunamiPreferences preferences;
    private final String userId;

    @Inject
    public TsunamiApiClient(TsunamiService service, TsunamiPreferences preferences) {
        this.service = service;
        this.preferences = preferences;
        this.userId = preferences.id.get();
    }

    public Observable<User> createUser() {
        CreateUserRequest request = new CreateUserRequest(userId);
        return service.createUser(request);
    }

    public Observable<Ripple> ripple(int waveId, double latitude, double longitude) {
        CreateRippleRequest request = new CreateRippleRequest(userId, waveId, latitude, longitude);
        return service.ripple(request);
    }

    public Observable<List<Wave>> getWaves(double latitude, double longitude) {
        return service.getWaves(userId, latitude, longitude);
    }

    public Observable<Wave> splash(String content, double latitude, double longitude) {
        SplashRequest request = new SplashRequest(userId, content, latitude, longitude);
        Timber.d("splash.");
        return service.splash(request);
    }
}
