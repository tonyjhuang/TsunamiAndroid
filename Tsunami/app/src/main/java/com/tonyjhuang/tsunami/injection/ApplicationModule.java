package com.tonyjhuang.tsunami.injection;

import android.content.Context;

import com.tonyjhuang.tsunami.TsunamiApplication;
import com.tonyjhuang.tsunami.api.models.parsers.TsunamiGson;
import com.tonyjhuang.tsunami.api.network.TsunamiApiClient;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 *  Place all application-scoped injections here. For instance, one-per-application singletons
 */
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
    TsunamiApiClient provideTsunamiApiClient(TsunamiGson gson) {
        return new TsunamiApiClient(application, gson);
    }

    @Provides
    @Singleton
    TsunamiGson provideTsunamiGson() {
        return new TsunamiGson();
    }

    /**
     * Allows the application module to provide the same dependencies as activity modules
     */
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ForApplication {
    }
}
