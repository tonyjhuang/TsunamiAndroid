package com.tonyjhuang.tsunami.injection;

import android.content.Context;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.api.network.TsunamiApiClient;
import com.tonyjhuang.tsunami.ui.main.MainActivity;
import com.tonyjhuang.tsunami.ui.main.RandomStringWavePresenter;
import com.tonyjhuang.tsunami.ui.main.TestEmitterPresenter;
import com.tonyjhuang.tsunami.ui.main.WavePresenterImpl;
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
    public WavePresenter provideWavePresenter(TsunamiApiClient api) {
        return new TestEmitterPresenter(api);
    }

}
