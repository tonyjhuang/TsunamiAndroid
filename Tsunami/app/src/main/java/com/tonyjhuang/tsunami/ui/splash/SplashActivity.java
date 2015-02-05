package com.tonyjhuang.tsunami.ui.splash;

import android.content.Intent;

import com.tonyjhuang.tsunami.injection.SplashModule;
import com.tonyjhuang.tsunami.utils.SingleFragmentActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tony on 2/5/15.
 */
public class SplashActivity extends SingleFragmentActivity {

    public static void startSplashActivity(TsunamiActivity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        activity.startActivityForResult(intent, TsunamiConstants.SPLASH_REQUEST_CODE);
    }

    @Override
    public TsunamiFragment getFragment() {
        return SplashFragment.getInstance();
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.asList(new SplashModule());
    }
}
