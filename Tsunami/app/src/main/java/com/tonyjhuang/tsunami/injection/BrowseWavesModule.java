package com.tonyjhuang.tsunami.injection;

import com.tonyjhuang.tsunami.ui.profile.waves.BrowseWavesActivity;
import com.tonyjhuang.tsunami.ui.profile.waves.BrowseWavesSingleWaveFragment;
import com.tonyjhuang.tsunami.ui.profile.waves.BrowseWavesViewPagerFragment;

import dagger.Module;

@Module(
        injects = {
                BrowseWavesActivity.class,
                BrowseWavesViewPagerFragment.class,
                BrowseWavesSingleWaveFragment.class
        },
        library = false,
        complete = false,
        addsTo = ApplicationModule.class
)
public class BrowseWavesModule {
}