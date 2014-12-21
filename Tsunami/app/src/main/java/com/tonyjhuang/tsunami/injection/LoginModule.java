package com.tonyjhuang.tsunami.injection;

import com.tonyjhuang.tsunami.ui.login.LoginActivity;

import dagger.Module;

/**
 * Created by tony on 12/21/14.
 */
@Module(
        injects = {
                LoginActivity.class
        },
        library = false,
        complete = false,
        addsTo = ApplicationModule.class
)
public class LoginModule {
}
