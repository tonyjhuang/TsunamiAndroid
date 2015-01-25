package com.tonyjhuang.tsunami.injection;

import com.tonyjhuang.tsunami.ui.profile.ProfileActivity;
import com.tonyjhuang.tsunami.ui.profile.ProfileFragment;

import dagger.Module;

/**
 * Created by tony on 12/21/14.
 */
@Module(
        injects = {
                ProfileActivity.class,
                ProfileFragment.class
        },
        addsTo = ApplicationModule.class,
        complete = false,
        library = false
)
public class ProfileModule {
}
