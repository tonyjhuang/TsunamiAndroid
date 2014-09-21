package com.tonyjhuang.tsunami.injection;


import com.tonyjhuang.tsunami.ui.main.contentview.ContentFragment;

import dagger.Module;

/**
 * Created by tonyjhuang on 8/17/14.
 */
@Module(
        injects = {
                ContentFragment.class
        },
        addsTo = ActivityModule.class,
        library = true
)
public class FragmentModule {
}
