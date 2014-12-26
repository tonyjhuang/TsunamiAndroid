package com.tonyjhuang.tsunami.ui.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;

import com.tonyjhuang.tsunami.R;

/**
 * Created by tony on 12/25/14.
 */
public class ProfileStatTextSwitcher extends TextSwitcher {
    private static ViewFactory factory;

    public ProfileStatTextSwitcher(Context context) {
        this(context, null);
    }

    public ProfileStatTextSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFactoryIfDoesntExit();
        setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        setFactory(factory);
    }

    private void initFactoryIfDoesntExit() {
        if (factory == null)
            factory = (() -> View.inflate(getContext(), R.layout.stub_profile_stat_num, null));
    }
}
