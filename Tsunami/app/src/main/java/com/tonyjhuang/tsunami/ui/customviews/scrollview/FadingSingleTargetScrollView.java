package com.tonyjhuang.tsunami.ui.customviews.scrollview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by tony on 12/30/14.
 */
public class FadingSingleTargetScrollView extends SingleTargetScrollView {
    public static final float FADE_ALPHA = 0.1f;
    public static final int FADE_ANIM_DURATION = 200;

    private ObjectAnimator alphaAnimator;

    public FadingSingleTargetScrollView(Context context) {
        super(context);
    }

    public FadingSingleTargetScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingSingleTargetScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (getCustomView() == null) return;
        getCustomView().setAlpha(getAlphaForPosition(t));
    }

    protected float getAlphaForPosition(int scrollY) {
        return 1f;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        if (!isTouchingView(ev)) {
            /**
             * We really only care about the MotionEvent if the user isn't touching the card view.
             */
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    fadeView(true);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    fadeView(false);
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (((!isTouchingView(ev) && !draggingInside) || draggingOutside))
            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    fadeView(false);
            }
        return super.onTouchEvent(ev);
    }

    private void fadeView(boolean fade) {
        if (getCustomView() == null) return;
        float appropriateAlpha = getAlphaForPosition(getScrollY());
        float startAlpha = getCustomView().getAlpha();
        float endAlpha = fade ? Math.min(FADE_ALPHA, appropriateAlpha) : appropriateAlpha;

        if (alphaAnimator != null && alphaAnimator.isRunning()) alphaAnimator.cancel();
        initAlphaAnimator(startAlpha, endAlpha).start();
    }

    private ObjectAnimator initAlphaAnimator(float startAlpha, float endAlpha) {
        alphaAnimator = ObjectAnimator.ofFloat(getCustomView(), "alpha", startAlpha, endAlpha)
                .setDuration(FADE_ANIM_DURATION);
        return alphaAnimator;
    }
}
