package com.tonyjhuang.tsunami.api.network;

import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.Wave;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * See http://square.github.io/retrofit/
 * The interface that our network service provides
 * TODO: potentially move away from all query params and use @Body params instead
 * Created by tonyjhuang on 8/17/14.
 */
public interface TsunamiService {

    /**
     * Get a user
     *
     * @param userId id of the user
     * @return Observable that will be pushed the requested user.
     */
    @GET("/users")
    Observable<User> getUser(@Query("user_id") long userId);

    /**
     * Create a new user. For now, we don't care if usernames collide.
     *
     * @param userName The username to associate with the new user
     * @return Observable that will be pushed the new user's ID
     */
    @POST("/users")
    Observable<Integer> createUser(@Query("username") String userName);

    /**
     * Get a list of ALL waves in our ocean. Probably not useful for production but
     * will be good to debug with.
     *
     * @return Observable that will be pushed a single list that contains all waves.
     */
    @GET("/ocean/waves")
    Observable<List<Wave>> getAllWaves();

    /**
     * Get waves given a user id and coordinates
     *
     * @param userId id of the user
     * @param lat    latitude of the current user
     * @param lon    longitude of the current user
     * @return Observable that will be pushed a single list of waves
     */
    @GET("/ocean/waves")
    Observable<List<Wave>> getWaves(@Query("user_id") long userId,
                                    @Query("lat") double lat,
                                    @Query("long") double lon);

    /**
     * Get the list of all ripples for a single wave.
     *
     * @param waveId The id of the wave you want the ripples for.
     * @return Observable that will be pushed a single list of ripple objects.
     */
    @GET("/ocean/ripples")
    Observable<List<Ripple>> getRipples(@Query("wave_id") long waveId);

    /**
     * Add a new ripple to a wave
     *
     * @param userId id of the current user that wants to ripple the wave
     * @param waveId id of the wave that is receiving the ripple
     * @param lat    latitude of the current user
     * @param lon    longitude of the current user
     * @return Observable that will be pushed the wave that was rippled.
     */
    @POST("/ocean")
    Observable<Wave> addRipple(@Query("user_id") long userId,
                               @Query("wave_id") long waveId,
                               @Query("lat") double lat,
                               @Query("long") double lon);

    /**
     * Splash a new text wave.
     *
     * @param userId  id of the current user that wants to splash
     * @param content text block of the new wave that's being splashed
     * @param lat     latitude of the current user
     * @param lon     longitude of the current user
     * @return Observable that will be pushed the new wave that was splashed
     */
    @POST("/splash/text")
    Observable<Wave> splashText(@Query("user_id") long userId,
                                @Query("content") String content,
                                @Query("lat") double lat,
                                @Query("long") double lon);
}
