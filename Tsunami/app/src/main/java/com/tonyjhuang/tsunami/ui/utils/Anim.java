package com.tonyjhuang.tsunami.ui.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by tony on 1/21/15.
 */
public class Anim {
    public static ObjectAnimator fadeIn(View view) {
        view.clearAnimation();
        ObjectAnimator animator = getAlphaAnimation(view, 0, 1f);
        animator.start();
        return animator;
    }

    public static ObjectAnimator fadeOut(View view) {
        view.clearAnimation();
        ObjectAnimator animator = getAlphaAnimation(view, view.getAlpha(), 0f);
        animator.start();
        return animator;
    }

    private static ObjectAnimator getAlphaAnimation(View view, float start, float end) {
        return ObjectAnimator.ofFloat(view, View.ALPHA, start, end)
                .setDuration(200);
    }
}
