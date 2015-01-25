package com.tonyjhuang.tsunami.injection;

import com.tonyjhuang.tsunami.ui.main.comments.CommentsActivity;
import com.tonyjhuang.tsunami.ui.main.comments.CommentsFragment;
import com.tonyjhuang.tsunami.ui.main.contentview.ContentCard;
import com.tonyjhuang.tsunami.ui.shared.PhotoViewActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.ui.main.MainActivity;
import com.tonyjhuang.tsunami.ui.main.MainWavePresenter;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.WaveProvider;
import com.tonyjhuang.tsunami.ui.main.mapview.MainWaveProvider;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapView;
import com.tonyjhuang.tsunami.ui.main.mapview.WaveMapViewImpl;
import com.tonyjhuang.tsunami.ui.drawer.NavDrawerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tony on 12/20/14.
 */
@SuppressWarnings("unused")
@Module(
        injects = {
                MainActivity.class,
                NavDrawerFragment.class,
                PhotoViewActivity.class,
                CommentsActivity.class,
                CommentsFragment.class,
                ContentCard.class
        },
        addsTo = ApplicationModule.class,
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
        return new MainWaveProvider(activity, api);
    }

}
