package com.tonyjhuang.tsunami.injection;

import com.tonyjhuang.tsunami.ui.main.contentview.cards.content.ContentCard;
import com.tonyjhuang.tsunami.ui.main.mapview.MainWaveMapView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.ui.profile.waves.BrowseWavesActivity;
import com.tonyjhuang.tsunami.ui.profile.waves.BrowseWavesSingleWaveFragment;
import com.tonyjhuang.tsunami.ui.profile.waves.BrowseWavesViewPagerFragment;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BrowseWavesActivity.class,
                BrowseWavesViewPagerFragment.class,
                BrowseWavesSingleWaveFragment.class,
                ContentCard.class
        },
        library = false,
        complete = false,
        addsTo = ApplicationModule.class
)
public class BrowseWavesModule {

    private TsunamiActivity activity;

    public BrowseWavesModule(TsunamiActivity activity) {
        this.activity = activity;
    }

    @Provides
    public WaveMapView provideWaveMapView() {
        return new MainWaveMapView(activity.getResources());
    }
}
