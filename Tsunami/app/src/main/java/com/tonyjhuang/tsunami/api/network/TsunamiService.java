package com.tonyjhuang.tsunami.api.network;

import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.requestbodies.CommentRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateRippleRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateUserRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.DismissWaveRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.SplashRequest;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * See http://square.github.io/retrofit/
 * Created by tonyjhuang on 8/17/14.
 */
public interface TsunamiService {

    /* USERS USERS USERS USERS USERS USERS USERS USERS */

    /**
     * Create a new user.
     */
    @POST("/users")
    Observable<User> createUser(@Body CreateUserRequest body);

    @GET("/users/stats")
    Observable<UserStats> getUserStats(@Query("user_id") long userId);

    /* RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE RIPPLE */

    /**
     * Ripple a wave.
     */
    @POST("/ripple")
    Observable<Ripple> ripple(@Body CreateRippleRequest body);

    /* OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN OCEAN */

    /**
     * Get the list of applicable waves that this user is in.
     */
    @GET("/ocean/local_waves")
    Observable<List<Wave>> getWaves(@Query("user_id") long userId,
                                    @Query("latitude") double latitude,
                                    @Query("longitude") double longitude);

    /**
     * Splash a new wave. For now the only content type supported is text.
     */
    @POST("/ocean/splash")
    Observable<Wave> splash(@Body SplashRequest body);

    /**
     * Dismiss a wave (mark as viewed).
     */
    @POST("/ocean/dismiss")
    Observable<Void> dismissWave(@Body DismissWaveRequest body);

    @POST("/comments")
    Observable<Wave> comment(@Body CommentRequest body);
}
