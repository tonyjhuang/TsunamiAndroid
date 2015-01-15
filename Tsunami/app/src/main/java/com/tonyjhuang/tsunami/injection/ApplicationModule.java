package com.tonyjhuang.tsunami.injection;

import android.content.Context;
import android.content.res.Resources;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.TsunamiApplication;
import com.tonyjhuang.tsunami.api.dal.TsunamiCache;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.api.network.TsunamiApiClient;
import com.tonyjhuang.tsunami.api.network.TsunamiService;
import com.tonyjhuang.tsunami.api.network.TsunamiServiceBuilder;
import com.tonyjhuang.tsunami.mock.MockTsunamiApiClient;
import com.tonyjhuang.tsunami.mock.reddit.RedditAndPicturesApiClient;
import com.tonyjhuang.tsunami.mock.reddit.RedditApiClient;
import com.tonyjhuang.tsunami.utils.TsunamiPreferences;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Place all application-scoped injections here. For instance, one-per-application singletons
 */
@SuppressWarnings("unused")
@Module(library = true)
public class ApplicationModule {
    TsunamiApplication application;

    public ApplicationModule(TsunamiApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    TsunamiService provideTsunamiService() {
        return TsunamiServiceBuilder.build();
    }

    @Provides
    @Singleton
    TsunamiApi provideTsunamiApi(TsunamiService service, TsunamiPreferences preferences, TsunamiCache cache) {
        return new RedditAndPicturesApiClient(application, service, preferences, cache);
    }

    @Provides
    @Singleton
    TsunamiCache provideTsunamiCache() {
        return new TsunamiCache(application);
    }

    @Provides
    @Singleton
    LocationInfo provideLocationInfo() {
        return new LocationInfo(application.getBaseContext());
    }

    @Provides
    @Singleton
    Resources provideResources() {
        return application.getResources();
    }

    @Provides
    @Singleton
    TsunamiPreferences provideTsunamiPreferences() {
        return new TsunamiPreferences(application);
    }

    /**
     * Allows the application module to provide the same dependencies as activity modules
     */
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ForApplication {
    }
}
