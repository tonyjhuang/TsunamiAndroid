package com.tonyjhuang.tsunami.injection;


import com.tonyjhuang.tsunami.ui.main.wave.mapview.WaveMapViewImpl;

import dagger.Module;

/**
 * Created by tonyjhuang on 8/17/14.
 */
@Module(
        injects = {
                WaveMapViewImpl.class
        },
        addsTo = ActivityModule.class,
        library = true
)
public class ViewModule {
}
