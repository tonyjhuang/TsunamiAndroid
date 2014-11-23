package com.tonyjhuang.tsunami.injection;

import dagger.ObjectGraph;

/**
 * Created by tony on 11/23/14.
 */
public interface Injector {
    public void inject(Object injectee);

    public ObjectGraph getObjectGraph();
}
