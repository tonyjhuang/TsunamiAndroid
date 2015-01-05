package com.tonyjhuang.tsunami.injection;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.tonyjhuang.tsunami.ui.login.LoginActivity;
import com.tonyjhuang.tsunami.ui.main.MainActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("unused")
@Module(
        addsTo = ApplicationModule.class,
        library = true
)
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ForActivity
    public Context provideContext() {
        return activity;
    }

    @Provides
    public LayoutInflater provideLayoutInflator() {
        return (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Allows the application module to provide the same dependencies as activity modules
     */
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ForActivity {
    }
}