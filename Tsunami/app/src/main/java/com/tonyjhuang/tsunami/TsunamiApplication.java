package com.tonyjhuang.tsunami;

import android.app.Application;

import com.tonyjhuang.tsunami.injection.ApplicationModule;

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
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }
}
