package com.tonyjhuang.tsunami.api.network;

import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateRippleRequest;
import com.tonyjhuang.tsunami.api.network.requestbodies.CreateUserRequest;
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

    /**
     * Create a new user.
     */
    @POST("/users")
    Observable<User> createUser(@Body CreateUserRequest body);

    /**
     * Ripple a wave.
     */
    @GET("/ocean/waves")
    Observable<Ripple> ripple(@Body CreateRippleRequest body);

    /**
     * Get the list of applicable waves that this user is in.
     */
    @GET("/ocean/ripples")
    Observable<List<Wave>> getWaves(@Query("guid") String guid,
                                    @Query("latitude") double latitude,
                                    @Query("longitude") double longitude);

    /**
     * Splash a new wave. For now the only content type supported is text.
     */
    @POST("/ocean/splash")
    Observable<Wave> splash(@Body SplashRequest body);
}
