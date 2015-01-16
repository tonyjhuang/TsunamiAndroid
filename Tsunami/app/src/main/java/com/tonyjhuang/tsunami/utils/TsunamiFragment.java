package com.tonyjhuang.tsunami.utils;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tonyjhuang.tsunami.injection.Injector;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Handles both pojo and android view injections (butterknife and dagger libraries)
 */
public abstract class TsunamiFragment extends Fragment implements Injector{

    private ObjectGraph objectGraph;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();


    /**
     * Override this method to declare your specific injection module for this TsunamiModule.
     * Since all activities are injected into and injectors, you have to provide at the very least
     * an empty module that injects into your particular subclass.
     */
    protected List<Object> getMyModules() {
        return Collections.emptyList();
    }

    @Override
    public void inject(Object injectee) {
        objectGraph.inject(injectee);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        objectGraph = ((TsunamiActivity) getActivity()).getObjectGraph().plus(getMyModules().toArray());
        inject(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }

    protected final void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected <T> void subscribe(Observable<T> observable, Observer<T> observer) {
        Subscription subscription = AndroidObservable.bindFragment(this, observable)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);

        compositeSubscription.add(subscription);
    }

    protected <T> void subscribe(Observable<T> observable, Action1<T> onNext) {
        Subscription subscription = AndroidObservable.bindFragment(this, observable)
                .subscribeOn(Schedulers.io())
                .subscribe(onNext);

        compositeSubscription.add(subscription);
    }

    protected <T> void subscribe(Observable<T> observable, Action1<T> onNext, Action1<Throwable> onError) {
        Subscription subscription = AndroidObservable.bindFragment(this, observable)
                .subscribeOn(Schedulers.io())
                .subscribe(onNext, onError);

        compositeSubscription.add(subscription);
    }
}
