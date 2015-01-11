package com.tonyjhuang.tsunami.injection;

import android.app.Activity;
import android.content.Context;

import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.mock.TestCelebrationPresenter;
import com.tonyjhuang.tsunami.ui.main.MainActivity;
import com.tonyjhuang.tsunami.ui.main.MainWavePresenter;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.WaveProvider;
import com.tonyjhuang.tsunami.ui.main.mapview.MainWaveProvider;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapViewImpl;
import com.tonyjhuang.tsunami.ui.nav.NavDrawerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tony on 12/20/14.
 */
@SuppressWarnings("unused")
@Module(
        injects = {
                MainActivity.class,
                NavDrawerFragment.class
        },
        addsTo = ActivityModule.class,
        complete = false
)
public class MainModule {

    private TsunamiActivity activity;

    public MainModule(TsunamiActivity activity) {
        this.activity = activity;
    }

    @Provides
    public WaveMapView provideWaveMapView() {
        return new WaveMapViewImpl(activity.getResources());
    }

    @Provides
    public WavePresenter provideWavePresenter(TsunamiApi api, WaveProvider waveProvider) {
        return new MainWavePresenter(api, activity, waveProvider);
    }

    @Provides
    public WaveProvider provideWaveProvider(TsunamiApi api) {
        return new MainWaveProvider(api);
    }

}
