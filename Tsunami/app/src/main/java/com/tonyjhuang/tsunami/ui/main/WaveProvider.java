package com.tonyjhuang.tsunami.ui.main;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;

import rx.Observable;

/**
 * Created by tony on 1/4/15.
 */
public interface WaveProvider {
    public Observable<Wave> getNextWave();

    public boolean hasNextWave();

    public void setLocationInfo(LocationInfo locationInfo);
}
