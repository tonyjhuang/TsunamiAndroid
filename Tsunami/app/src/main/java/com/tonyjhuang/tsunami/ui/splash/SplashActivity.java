package com.tonyjhuang.tsunami.ui.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;

/**
 * Created by tonyjhuang on 9/13/14.
 */
public class SplashActivity extends TsunamiActivity {
    public static final int SPLASH_ACTIVITY_REQUEST_CODE = 1;

    public static void startSplashActivity(Activity activity) {
        activity.startActivityForResult(
                new Intent(activity, SplashActivity.class),
                SPLASH_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
