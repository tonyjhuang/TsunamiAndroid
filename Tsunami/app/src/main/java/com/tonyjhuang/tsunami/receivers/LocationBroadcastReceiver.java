package com.tonyjhuang.tsunami.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.tonyjhuang.tsunami.logging.Timber;

/**
 * Created by tony on 1/28/15.
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {
    private Callbacks callbacks;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("location received");
        // extract the location info in the broadcast
        LocationInfo locationInfo = (LocationInfo) intent
                .getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);

        if (callbacks != null)
            callbacks.onLocationReceived(locationInfo);
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public static interface Callbacks {
        public void onLocationReceived(LocationInfo locationInfo);
    }
}
