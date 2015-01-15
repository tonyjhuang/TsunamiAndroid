package com.tonyjhuang.tsunami.ui.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.logging.Timber;

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
        setInAnimation(AnimationUtils.loadAnimation(getContext().getApplicationContext(), android.R.anim.fade_in));
        setOutAnimation(AnimationUtils.loadAnimation(getContext().getApplicationContext(), android.R.anim.fade_out));
        setFactory(factory);
    }

    private void initFactoryIfDoesntExit() {
        if (factory == null)
            factory = (() -> View.inflate(getContext(), R.layout.stub_profile_stat_num, null));
    }


    @Override
    public void setText(CharSequence text) {
        if (getInAnimation().hasStarted() && !getInAnimation().hasEnded()) {
            getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setText(text);
                    getInAnimation().setAnimationListener(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            if(getCurrentText() != null && !getCurrentText().equals(text)) {
                super.setText(text);
            } else {
                Timber.v("trying to re-set the current text. ignoring.");
            }
        }
    }

    private CharSequence getCurrentText() {
        return ((TextView) super.getCurrentView()).getText();
    }
}
