package com.tonyjhuang.tsunami.mock.reddit;

import android.app.Application;

import com.tonyjhuang.tsunami.api.dal.TsunamiCache;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiService;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.mock.FileReader;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.util.List;
import java.util.Random;

import rx.Observable;

/**
 * Created by tony on 1/12/15.
 */
public class RedditAndPicturesApiClient extends RedditApiClient {
    private Application application;

    public RedditAndPicturesApiClient(Application application,
                                      TsunamiService service,
                                      TsunamiPreferences preferences,
                                      TsunamiCache cache) {
        super(application, service, preferences, cache);
        this.application = application;
    }

    public String getRandomImageUrl() {
        String images = FileReader.readFile(application, "images.txt");
        String[] imageUrls = images.split(",");
        String ret = imageUrls[new Random().nextInt(imageUrls.length)];
        Timber.d(ret);
        return ret;
    }

    @Override
    public Observable<List<Wave>> getWaves(double latitude, double longitude) {
        return super.getWaves(latitude, longitude)
                .flatMap(Observable::from)
                .map((wave) -> {
                    if (new Random().nextInt(5) <= 4) wave.convertToImageLink(getRandomImageUrl());
                    return wave;
                }).toList();
    }
}
