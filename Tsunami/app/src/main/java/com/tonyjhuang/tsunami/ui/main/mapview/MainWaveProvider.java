package com.tonyjhuang.tsunami.ui.main.mapview;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.api.parsers.TsunamiGson;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.WaveProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by tony on 1/4/15.
 */
public class MainWaveProvider implements WaveProvider {

    private Context context;
    private TsunamiApi api;
    private LocationInfo locationInfo;

    List<Wave> waves = new ArrayList<>();
    private int index = 0;

    @Inject
    public MainWaveProvider(Context context, TsunamiApi api) {
        this.context = context;
        this.api = api;
    }

    /**
     * gets the next set of waves, replaces our local cache and emits an Object representing success.
     */
    protected Observable<Object> getMoreWaves() {
        if(locationInfo == null) locationInfo = new LocationInfo(context);
        return api.getLocalWaves(locationInfo.lastLat, locationInfo.lastLong)
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
            // No waves...
            return Observable.error(new RuntimeException("Failed to retrieve new waves"));
        } else if (index < waves.size()) {
            return Observable.just(waves.get(index++));
        } else {
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

    @Override
    public String getMemento() {
        MainWaveProviderMemento memento = new MainWaveProviderMemento();
        memento.waves = waves;
        memento.index = index;
        return memento.toString();
    }

    @Override
    public void fromMemento(String string) {
        MainWaveProviderMemento memento = TsunamiGson.gson.fromJson(string, MainWaveProviderMemento.class);
        waves = memento.waves;
        index = memento.index;
    }

    private static class MainWaveProviderMemento {
        @Expose
        List<Wave> waves;
        @Expose
        int index;

        public String toString() {
            return TsunamiGson.gson.toJson(this);
        }
    }
}
