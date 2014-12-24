package com.tonyjhuang.tsunami;

import android.app.AlarmManager;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.tonyjhuang.tsunami.injection.ApplicationModule;
import com.tonyjhuang.tsunami.injection.Injector;
import com.tonyjhuang.tsunami.logging.Timber;

import dagger.ObjectGraph;

/**
 * Created by tonyhuang on 7/29/14.
 */
public class TsunamiApplication extends Application implements Injector {
    private static final int TWO_HOURS = 2 * 60 * 60 * 1000;
    private static final int ONE_MINUTE = 60 * 1000;

    private ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationGraph = ObjectGraph.create(new ApplicationModule(this));

        LocationLibrary.showDebugOutput(true);

        Timber.plant(new Timber.DebugTree());

        try {
            LocationLibrary.initialiseLibrary(getBaseContext(),
                    ONE_MINUTE, // check location frequency
                    100000000,
                    true,
                    "com.tonyjhuang.tsunami");
        } catch (UnsupportedOperationException e) {
            Timber.e("No location providers", e);
            Toast.makeText(this, "Sorry, but you need a device that has gps to use this app!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return applicationGraph;
    }

    @Override
    public void inject(Object injectee) {
        applicationGraph.inject(injectee);
    }
}
