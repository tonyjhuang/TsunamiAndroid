package com.tonyjhuang.tsunami.api.network;

import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateRippleRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateUserRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.SplashRequest;

import java.util.List;

import rx.Observable;

/**
 * Created by tony on 12/22/14.
 */
public interface TsunamiApi {
    /* USERS USERS USERS USERS USERS USERS USERS USERS */
    boolean isLoggedIn();

    Observable<User> getCurrentUser();

    Observable<Long> getCurrentUserId();

    Observable<User> createUser();

    Observable<UserStats> getCurrentUserStats();

    Observable<UserStats> getUserStats(long userId);

    Observable<List<Wave>> getCurrentUserWaves();

    Observable<List<Wave>> getUserWaves(long userId);

    /* RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE */

    Observable<Ripple> ripple(long waveId, double latitude, double longitude);

    /* OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN */

    Observable<List<Wave>> getLocalWaves(double latitude, double longitude);

    Observable<Wave> getWave(long waveId);

    Observable<Wave> splash(String caption,
                                   WaveContent.ContentType contentType,
                                   double latitude,
                                   double longitude);

    public Observable<Void> dismissWave(long waveId);

    public Observable<Wave> comment(long waveId, String comment);
}
