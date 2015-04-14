package com.tonyjhuang.tsunami.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.localytics.android.Localytics;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.injection.Injector;
import com.tonyjhuang.tsunami.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
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
 * Handles all of the boring initialization stuff we would want for every activity
 * This includes injection, ui styling, logging setup
 */
public abstract class TsunamiActivity extends ActionBarActivity implements
        Session.StatusCallback,
        Injector {
    private ObjectGraph objectGraph;

    /**
     * See https://developers.facebook.com/docs/android/login-with-facebook/v2.1
     */
    private UiLifecycleHelper uiHelper;

    /**
     * Contains all active subscriptions that we want to listen to.
     */
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, this);
        uiHelper.onCreate(savedInstanceState);

        Localytics.registerPush(getString(R.string.localytics_app_key));

        objectGraph = ((TsunamiApplication) getApplication())
                .getObjectGraph().plus(getModules().toArray());
        inject(this);
    }

    @Override
    public void setContentView(int resourceLayoutId) {
        super.setContentView(resourceLayoutId);
        ButterKnife.inject(this);
    }

    /**
     * Override this method to declare your specific injection module for this TsunamiModule.
     * Since all activities are injected into and injectors, you have to provide at the very least
     * an empty module that injects into your particular subclass.
     */
    protected abstract List<Object> getModules();

    @Override
    public void inject(Object object) {
        if (object != null)
            objectGraph.inject(object);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    protected void logout() {
        Session.getActiveSession().closeAndClearTokenInformation();
    }

    /**
     * Facebook Ui Lifecycle Helper callback interface
     */
    @Override
    public void call(Session session, SessionState state, Exception exception) {
        onSessionStateChange(session, state, exception);
    }

    /**
     * Override this if you want to register your activity for facebook session state changes
     */
    public void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            //Timber.d("Logged in...");
        } else if (state.isClosed() && !isFinishing()) {
            LoginActivity.startLoginActivity(this);
            finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        compositeSubscription.clear();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // for localytics
        setIntent(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    protected <T> void subscribe(Observable<T> observable, Observer<T> observer) {
        Subscription subscription = AndroidObservable.bindActivity(this, observable)
                .subscribeOn(Schedulers.newThread())
                .subscribe(observer);

        compositeSubscription.add(subscription);
    }

    protected <T> void subscribe(Observable<T> observable, Action1<T> onNext) {
        Subscription subscription = AndroidObservable.bindActivity(this, observable)
                .subscribeOn(Schedulers.newThread())
                .subscribe(onNext);

        compositeSubscription.add(subscription);
    }

    protected <T> void subscribe(Observable<T> observable, Action1<T> onNext, Action1<Throwable> onError) {
        Subscription subscription = AndroidObservable.bindActivity(this, observable)
                .subscribeOn(Schedulers.newThread())
                .subscribe(onNext, onError);

        compositeSubscription.add(subscription);
    }

    protected final void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
