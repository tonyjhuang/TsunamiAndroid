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

    public Observable<User> createUser();

    public Observable<UserStats> getCurrentUserStats();

    public Observable<UserStats> getUserStats(String userId);

    /* RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE */

    public Observable<Ripple> ripple(long waveId, double latitude, double longitude);

    /* OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN */

    public Observable<List<Wave>> getLocalWaves(double latitude, double longitude);

    public Observable<Wave> getWave(long waveId);

    public Observable<Wave> splash(String title,
                                   String body,
                                   WaveContent.ContentType contentType,
                                   double latitude,
                                   double longitude);

    public Observable<Void> dismissWave(long waveId);

    public Observable<Wave> comment(long waveId, String comment);
}
