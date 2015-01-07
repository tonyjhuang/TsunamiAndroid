package com.tonyjhuang.tsunami.ui.main.mapview;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.WaveProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;

/**
 * Created by tony on 1/4/15.
 */
public class MainWaveProvider implements WaveProvider {

    private TsunamiApi api;
    private LocationInfo locationInfo;

    List<Wave> waves = new ArrayList<>();
    private int index = 0;

    public MainWaveProvider(TsunamiApi api) {
        this.api = api;
    }

    /**
     * gets the next set of waves, replaces our local cache and emits an Object representing success.
     */
    protected Observable<Object> getMoreWaves() {
        return api.getWaves(locationInfo.lastLat, locationInfo.lastLong)
                .map((waves) -> {
                    Timber.d("got " + waves.size() + " new waves!");
                    index = 0;
                    MainWaveProvider.this.waves = waves;
                    return new Object();
                });
    }

    @Override
    public boolean hasNextWave() {
        return index < waves.size();
    }

    @Override
    public Observable<Wave> getNextWave() {
        return getNextWave(false);
    }

    /**
     * Observable that either returns the next wave to show or throw an error.
     *
     * @param retry should always call this method with false. Used to stop infinite loops.
     */
    protected Observable<Wave> getNextWave(boolean retry) {
        if (retry && index >= waves.size()) {
            Timber.e("errored out trying to get new waves");
            return Observable.error(new RuntimeException("Failed to retrieve new waves"));
        } else if (index < waves.size()) {
            Timber.d("returning " + index + " out of " + waves.size());
            return Observable.just(waves.get(index++));
        } else {
            Timber.d("no more waves, retrying.");
            return getMoreWaves().flatMap((object) -> getNextWave(true));
        }
    }

    @Override
    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
        invalidateWaves();
    }

    public void invalidateWaves() {
        int pos = 0;
        Iterator<Wave> iter = waves.iterator();
        while (iter.hasNext()) {
            Wave wave = iter.next();
            if (pos++ < index + 1) continue; // Only invalidate waves we haven't seen yet.
            if (!wave.isValidFor(locationInfo.lastLat, locationInfo.lastLong)) iter.remove();
        }
    }
}
