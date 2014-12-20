package com.tonyjhuang.tsunami;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Handles both pojo and android view injections (butterknife and dagger libraries)
 */
public abstract class TsunamiFragment extends Fragment {

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TsunamiActivity) getActivity()).inject(this);
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

    @SuppressWarnings("unchecked")
    protected void subscribe(Observable observable, Observer observer) {
        Subscription subscription = AndroidObservable.bindFragment(this, observable)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);

        compositeSubscription.add(subscription);
    }
}
