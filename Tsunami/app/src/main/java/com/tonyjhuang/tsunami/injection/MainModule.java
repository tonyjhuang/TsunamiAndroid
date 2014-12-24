package com.tonyjhuang.tsunami.injection;

import android.content.Context;

import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.mock.TestCelebrationPresenter;
import com.tonyjhuang.tsunami.ui.main.MainActivity;
import com.tonyjhuang.tsunami.ui.main.MainWavePresenter;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapViewImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tony on 12/20/14.
 */
@Module(
        injects = {
                MainActivity.class
        },
        addsTo = ActivityModule.class,
        complete = false
)
public class MainModule {

    private Context context;

    public MainModule(Context context) {
        this.context = context;
    }

    @Provides
    public WaveMapView provideWaveMapView() {
        return new WaveMapViewImpl(context.getResources());
    }

    @Provides
    public WavePresenter provideWavePresenter(TsunamiApi api) {
        return new MainWavePresenter(api);
    }

}
