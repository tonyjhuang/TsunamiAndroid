package com.tonyjhuang.tsunami;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.tonyjhuang.tsunami.injection.ActivityModule;
import com.tonyjhuang.tsunami.injection.FragmentModule;
import com.tonyjhuang.tsunami.ui.customviews.TypefaceSpan;
import com.tonyjhuang.tsunami.ui.login.LoginActivity;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

/**
 * Handles all of the boring initialization stuff we would want for every activity
 * This includes injection, ui styling, logging setup
 */
public abstract class TsunamiActivity extends Activity implements Session.StatusCallback {
    private ObjectGraph objectGraph;

    /**
     * See https://developers.facebook.com/docs/android/login-with-facebook/v2.1
     */
    private UiLifecycleHelper uiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, this);
        uiHelper.onCreate(savedInstanceState);

        objectGraph = ((TsunamiApplication) getApplication())
                .getApplicationGraph().plus(getModules().toArray());
        inject(this);

        /**
         * Color status bar and navigation bar if we're on at least kitkat.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarAlpha(0x000000);
            tintManager.setNavigationBarAlpha(0x000000);
            // enable status bar tint
             tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
        }
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            SpannableString titleSpannable = new SpannableString(title);
            TypefaceSpan titleSpan = new TypefaceSpan(this, "Oswald-Bold");
            titleSpannable.setSpan(titleSpan,
                    0,
                    titleSpannable.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(titleSpannable);
        }
    }

    @Override
    public void setContentView(int resourceLayoutId) {
        super.setContentView(resourceLayoutId);
        ButterKnife.inject(this);
    }

    /**
     * allows subclasses to define their own modules. just make sure you
     * append your list of modules to the super call
     */
    protected List<Object> getModules() {
        return Arrays.asList(new ActivityModule(this), new FragmentModule());
    }

    public void inject(Object object) {
        if (object != null)
            objectGraph.inject(object);
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

    protected void logout() {
        Session.getActiveSession().closeAndClearTokenInformation();
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
