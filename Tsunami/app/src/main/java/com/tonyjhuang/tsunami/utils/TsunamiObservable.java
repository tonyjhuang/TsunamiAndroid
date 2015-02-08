package com.tonyjhuang.tsunami.utils;

import com.tonyjhuang.tsunami.logging.Timber;

import rx.functions.Action1;

/**
 * Created by tony on 2/8/15.
 */
public class TsunamiObservable {
    public static Action1<Throwable> timberErrorLogger() {
        return (throwable) -> Timber.e(throwable, "ruh roh");
    }
}
