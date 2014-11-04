package com.tonyjhuang.tsunami;

import android.app.Application;
import android.widget.Toast;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.tonyjhuang.tsunami.injection.ApplicationModule;
import com.tonyjhuang.tsunami.logging.Timber;

import dagger.ObjectGraph;

/**
 * Created by tonyhuang on 7/29/14.
 */
public class TsunamiApplication extends Application {

    private ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationGraph = ObjectGraph.create(new ApplicationModule(this));

        LocationLibrary.showDebugOutput(true);

        try {
            LocationLibrary.initialiseLibrary(getBaseContext(), 60 * 1000, 2 * 60 * 1000, "com.tonyjhuang.tsunami");
        } catch (UnsupportedOperationException e) {
            Timber.e("No location providers", e);
            Toast.makeText(this, "Sorry, but you need a device that has gps to use this app!", Toast.LENGTH_LONG).show();
        }
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }
}
