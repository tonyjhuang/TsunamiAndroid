package com.tonyjhuang.tsunami.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.Session;
import com.facebook.SessionState;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.ui.main.MainActivity;

/**
 * Created by tonyjhuang on 8/17/14.
 */
public class LoginActivity extends TsunamiActivity {

    public static void startLoginActivity(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened() && !isFinishing()) {
            MainActivity.startMainActivity(this);
            finish();
        }
    }
}
