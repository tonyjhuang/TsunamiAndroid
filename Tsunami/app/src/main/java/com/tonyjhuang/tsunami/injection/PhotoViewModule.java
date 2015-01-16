package com.tonyjhuang.tsunami.injection;

import com.tonyjhuang.tsunami.ui.shared.PhotoViewActivity;

import dagger.Module;

/**
 * Created by tony on 1/15/15.
 */
@Module(
        injects = {
                PhotoViewActivity.class
        },
        addsTo = ActivityModule.class,
        complete = false,
        library = false
)
public class PhotoViewModule {
}