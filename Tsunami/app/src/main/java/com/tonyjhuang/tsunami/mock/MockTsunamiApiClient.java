package com.tonyjhuang.tsunami.mock;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.dal.TsunamiCache;
import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.api.network.TsunamiService;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by tony on 12/22/14.
 */
@Singleton
public class MockTsunamiApiClient implements TsunamiApi {

    private LocationInfo locationInfo;
    /**
     * Our Random seed that will help us make mock ripples.
     */
    private Random random = new Random();

    /**
     * Random string generators for making debugging waves. Creates titles and body text.
     */
    private RandomString randomTitleGen = new RandomString(16);
    private RandomString randomTextGen = new RandomString(128);

    @SuppressWarnings("unused")
    @Inject
    public MockTsunamiApiClient(Application application,
                                TsunamiService service,
                                TsunamiPreferences preferences,
                                TsunamiCache cache) {
        locationInfo = new LocationInfo(application);
        locationInfo.refresh(application);
    }

    @Override
    public Observable<User> createUser() {
        return Observable.just(User.createDebugUser(null));
    }

    @Override
    public Observable<UserStats> getCurrentUserStats() {
        return getUserStats(null);
    }

    private UserStats lastUserStats;

    @Override
    public Observable<UserStats> getUserStats(String userId) {
        Observable<UserStats> apiCall =
                Observable.just(UserStats.createDebugUserStats())
                        .delay(2, TimeUnit.SECONDS)
                        .doOnNext((userStats) -> lastUserStats = userStats);

        Observable<UserStats> cacheFetch;
        if (lastUserStats == null) {
            cacheFetch = Observable.empty();
        } else {
            cacheFetch = Observable.just(lastUserStats);
        }
        return Observable.concat(cacheFetch, apiCall);
    }

    @Override
    public Observable<Ripple> ripple(long waveId, double latitude, double longitude) {
        return Observable.just(Ripple.createDebugRipple(latitude, longitude));
    }

    @Override
    public Observable<List<Wave>> getLocalWaves(double latitude, double longitude) {
        List<Wave> waves = new ArrayList<>();
        for (int i = 0; i < randInt(); i++) {
            waves.add(generateRandomWave());
        }
        return Observable.just(waves);
    }

    @Override
    public Observable<Wave> getWave(long waveId) {
        return null;
    }

    @Override
    public Observable<Wave> comment(long waveId, String comment) {
        return null;
    }

    @Override
    public Observable<Wave> splash(String title,
                                   String body,
                                   WaveContent.ContentType contentType,
                                   double latitude,
                                   double longitude) {
        return Observable.just(generateRandomWave());
    }

    @Override
    public Observable<Void> dismissWave(long waveId) {
        return Observable.just(null);
    }


    /**
     * ========================= utility =========================
     */

    @SuppressWarnings("unchecked")
    protected Wave generateRandomWave() {
        return Wave.createDebugWave(randomTitleGen.nextString(),
                randomTextGen.nextString(),
                generateRandomRipples(),
                User.createDebugUser(null),
                Collections.EMPTY_LIST);
    }

    protected int randInt() {
        return (int) Math.max(Math.abs((100 * random.nextGaussian())), 1);
    }


    protected double randomDoubleInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    protected LatLng getRandomLatLng(double lat, double lng) {
        return new LatLng(
                lat + randomDoubleInRange(-0.02, 0.02),
                lng + randomDoubleInRange(-0.02, 0.02));
    }

    protected List<Ripple> generateRandomRipples() {
        return generateRandomRipples(randInt());
    }

    protected List<Ripple> generateRandomRipples(int numRipples) {
        List<Ripple> ripples = new ArrayList<>();
        List<LatLng> latLngs = getRandomLatLngs(numRipples);
        for (LatLng latLng : latLngs) {
            ripples.add(Ripple.createDebugRipple(latLng.latitude, latLng.longitude));
        }
        return ripples;

    }

    protected List<LatLng> getRandomLatLngs(int num) {
        ArrayList<LatLng> ripples = new ArrayList<>();
        LatLng last = null;

        for (int i = 0; i < num; i++) {
            if (last == null) {
                if (locationInfo == null) {
                    last = getRandomLatLng(42.331665, -71.108093);
                } else {
                    last = getRandomLatLng(locationInfo.lastLat, locationInfo.lastLong);
                }
            } else {
                last = getRandomLatLng(last.latitude, last.longitude);
            }
            ripples.add(last);
        }
        return ripples;
    }

}
