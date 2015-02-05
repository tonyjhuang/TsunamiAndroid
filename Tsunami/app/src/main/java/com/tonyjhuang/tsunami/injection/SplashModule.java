package com.tonyjhuang.tsunami.injection;

import com.tonyjhuang.tsunami.ui.splash.SplashActivity;
import com.tonyjhuang.tsunami.ui.splash.SplashFragment;

import dagger.Module;

/**
 * Created by tony on 2/5/15.
 */
@Module(
        injects = {
                SplashActivity.class,
                SplashFragment.class
        },
        addsTo = ApplicationModule.class,
        complete = false,
        library = false
)
public class SplashModule {
}
