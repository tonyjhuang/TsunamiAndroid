package com.tonyjhuang.tsunami.utils;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tony on 12/20/14.
 */
public class RxHelper {

    @SuppressWarnings("unchecked")
    public static Subscription bindAsync(Observable observable, Observer observer) {
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static <T> Subscription bindAsync(Observable<T> observable, Action1<T> action) {
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    public static <T> Subscription bindAsync(Observable<T> observable, Action1<T> onNext, Action1<Throwable> onError) {
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

}
