package com.tonyjhuang.tsunami.injection;

import com.tonyjhuang.tsunami.ui.profile.ProfileActivity;

import dagger.Module;

/**
 * Created by tony on 12/21/14.
 */
@Module(
        injects = {
                ProfileActivity.class
        },
        addsTo = ActivityModule.class,
        complete = false,
        library = false
)
public class ProfileModule {
}
