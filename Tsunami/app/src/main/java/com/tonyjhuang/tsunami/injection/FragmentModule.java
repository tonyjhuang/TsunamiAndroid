package com.tonyjhuang.tsunami.injection;


import com.tonyjhuang.tsunami.ui.main.wave.WaveFragment;

import dagger.Module;

/**
 * Created by tonyjhuang on 8/17/14.
 */
@Module(
        injects = {
                WaveFragment.class
        },
        addsTo = ActivityModule.class,
        library = true
)
public class FragmentModule {
}
