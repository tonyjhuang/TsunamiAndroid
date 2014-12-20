package com.tonyjhuang.tsunami.ui.main;

import com.google.android.gms.maps.model.LatLng;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApiClient;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.SplashCard;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.WaveContentView;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WMVFinishSplashingCallback;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WaveMapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rx.Observer;

/**
 * Created by tony on 12/20/14.
 */
public class RandomStringWavePresenter implements WavePresenter {

    private WaveContentView contentView;
    private WaveMapView mapView;

    private TsunamiApiClient api;
    private LocationInfo locationInfo;

    /**
     * Wave that we're hiding while the user is in the process of splashing a new Wave.
     */
    private Wave cachedDuringSplash;

    /**
     * Our Random seed that will help us make mock ripples.
     */
    private Random random = new Random();

    /**
     * Random string generators for making debugging waves. Creates titles and body text.
     */
    RandomString randomTitleGen = new RandomString(16);
    RandomString randomTextGen = new RandomString(128);

    public RandomStringWavePresenter(TsunamiApiClient api) {
        this.api = api;
    }

    public void setContentView(WaveContentView contentView) {
        this.contentView = contentView;
        contentView.setPresenter(this);
    }

    public void setMapView(WaveMapView mapView) {
        this.mapView = mapView;
        mapView.setPresenter(this);
    }

    private void displayNewWave() {
        List<Ripple> ripples = new ArrayList<>();
        List<LatLng> latLngs = getRandomLatLngs();
        for (LatLng latLng : latLngs) {
            ripples.add(Ripple.createDebugRipple(latLng.latitude, latLng.longitude));
        }

        Wave randomWave = Wave.createDebugWave(randomTitleGen.nextString(),
                randomTextGen.nextString(),
                ripples);
        displayWave(randomWave);
    }

    private void displayWave(Wave wave) {
        contentView.showContentCard(wave);
        mapView.displayWave(wave);
    }

    @Override
    public void onContentSwipedUp() {
        Timber.d("onContentSwipedUp");
        displayNewWave();
    }

    @Override
    public void onContentSwipedDown() {
        Timber.d("onContentSwipedDown");
        displayNewWave();
    }

    @Override
    public void onSplashSwipedUp() {
        Timber.d("onSplashSwipedUp");
        SplashCard.SplashContent splashContent = contentView.retrieveSplashContent();
        api.splash(splashContent.title, locationInfo.lastLat, locationInfo.lastLong).subscribe(new Observer<Wave>() {
            @Override
            public void onCompleted() {
                Timber.d("oncompleted");
            }

            @Override
            public void onError(Throwable e) {
                Timber.d("onerror");
            }

            @Override
            public void onNext(Wave wave) {
                Timber.d("onnext");
            }
        });
        mapView.finishSplashing(new WMVFinishSplashingCallback() {
            @Override
            public void onFinishSplashing() {
                Timber.d("in callback..");
                contentView.showContentCard(cachedDuringSplash);
                mapView.displayWave(cachedDuringSplash);
            }
        });
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        mapView.cancelSplashing();
        contentView.showContentCard(cachedDuringSplash);
        mapView.displayWave(cachedDuringSplash);
    }

    @Override
    public void onBeginSplashButtonClicked() {
        cachedDuringSplash = contentView.getContentWave();
        contentView.clearSplashCard();
        contentView.showSplashCard();
        mapView.displaySplashing();
    }

    @Override
    public void onCancelSplashButtonClicked() {
        mapView.cancelSplashing();
        contentView.showContentCard(cachedDuringSplash);
        mapView.displayWave(cachedDuringSplash);

    }

    @Override
    public void onSendSplashButtonClicked() {

    }

    @Override
    public void onProfileButtonClicked() {

    }

    @Override
    public void onLocationUpdate(LocationInfo newLocationInfo) {
        locationInfo = newLocationInfo;
        Timber.d("locationInfo: lat " + newLocationInfo.lastLat + ", lon " + newLocationInfo.lastLong);
        mapView.setCurrentLocation(locationInfo);

        if (contentView.getContentWave() == null && !contentView.isShowingSplashCard())
            displayNewWave();
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

        int numRandom = (int) Math.max(2 + Math.abs((6 * random.nextGaussian())), 0);

        for (int i = 0; i < numRandom; i++) {
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
