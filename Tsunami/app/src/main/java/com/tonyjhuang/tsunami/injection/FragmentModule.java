package com.tonyjhuang.tsunami.injection;


import com.tonyjhuang.tsunami.ui.main.ContentFragment;
import com.tonyjhuang.tsunami.ui.splash.SplashFragment;

import dagger.Module;

/**
 * Created by tonyjhuang on 8/17/14.
 */
@Module(
        injects = {
                ContentFragment.class,
                SplashFragment.class
        },
        addsTo = ActivityModule.class,
        library = true
)
public class FragmentModule {
}
