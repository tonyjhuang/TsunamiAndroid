package com.tonyjhuang.tsunami.mock;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.api.network.TsunamiService;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public MockTsunamiApiClient(Application application, TsunamiService service, TsunamiPreferences preferences) {
        locationInfo = new LocationInfo(application);
        locationInfo.refresh(application);
    }

    @Override
    public Observable<User> createUser() {
        return Observable.just(new User());
    }

    @Override
    public Observable<Ripple> ripple(long waveId, double latitude, double longitude) {
        LatLng latLng = getRandomLatLng(42.331665, -71.108093); // mission hill?
        return Observable.just(Ripple.createDebugRipple(latLng.latitude, latLng.longitude));
    }

    @Override
    public Observable<List<Wave>> getWaves(double latitude, double longitude) {
        List<Wave> waves = new ArrayList<>();
        for (int i = 0; i < randInt(); i++) {
            waves.add(generateRandomWave());
        }
        return Observable.just(waves);
    }

    @Override
    public Observable<Wave> splash(String title, String body, double latitude, double longitude) {
        return Observable.just(generateRandomWave());
    }

    private Wave generateRandomWave() {
        List<Ripple> ripples = new ArrayList<>();
        List<LatLng> latLngs = getRandomLatLngs();
        for (LatLng latLng : latLngs) {
            ripples.add(Ripple.createDebugRipple(latLng.latitude, latLng.longitude));
        }

        return Wave.createDebugWave(randomTitleGen.nextString(),
                randomTextGen.nextString(),
                ripples);
    }


    public static class RandomString {

        private static final char[] symbols;

        static {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();

        private final char[] buf;

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }

    /**
     * ========================= utility =========================
     */

    private int randInt() {
        return (int) Math.max(2 + Math.abs((6 * random.nextGaussian())), 0);
    }


    private double randomDoubleInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private LatLng getRandomLatLng(double lat, double lng) {
        return new LatLng(
                lat + randomDoubleInRange(-0.025, 0.025),
                lng + randomDoubleInRange(-0.025, 0.025));
    }

    private List<LatLng> getRandomLatLngs() {
        ArrayList<LatLng> ripples = new ArrayList<LatLng>();
        LatLng last = null;

        for (int i = 0; i < randInt(); i++) {
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