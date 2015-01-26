package com.tonyjhuang.tsunami.ui.main;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.models.Wave;

import rx.Observable;

/**
 * Created by tony on 1/4/15.
 */
public interface WaveProvider {
    /**
     * Returns the next wave in the list to show to the user. If there are no waves,
     * then this passes null to the subscriber.
     */
    public Observable<Wave> getNextWave();

    /**
     * Do we have a wave to show the next user? Returns false if we have to hit the network,
     * true if we can return a wave synchronously.
     */
    public boolean hasNextWave();

    /**
     * Updates the location for this WaveProvider.
     */
    public void setLocationInfo(LocationInfo locationInfo);

    public String getMemento();

    public void fromMemento(String string);
}
