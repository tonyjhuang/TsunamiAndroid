package com.tonyjhuang.tsunami.mock;

import android.content.Context;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.ui.main.WaveProvider;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by tony on 1/26/15.
 */
public class NoWaveWaveProvider implements WaveProvider {

    @Inject
    public NoWaveWaveProvider(Context context, TsunamiApi api) {
    }

    @Override
    public Observable<Wave> getNextWave() {
        return Observable.just(null);
    }

    @Override
    public boolean hasNextWave() {
        return false;
    }

    @Override
    public void setLocationInfo(LocationInfo locationInfo) {

    }

    @Override
    public String getMemento() {
        return "";
    }

    @Override
    public void fromMemento(String string) {

    }
}
