package com.tonyjhuang.tsunami.ui.splash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiFragment;

/**
 * Created by tonyjhuang on 9/13/14.
 */
public class SplashFragment extends TsunamiFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }
}
