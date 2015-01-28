package com.tonyjhuang.tsunami.mock;

import android.content.Context;
import android.os.Bundle;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.WaveProvider;

import java.util.concurrent.TimeUnit;

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
        Timber.d("getNextWave");
        Observable<Wave> o = Observable.just(null);
        return o.delay(1, TimeUnit.SECONDS);
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

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outParcel) {

    }
}
