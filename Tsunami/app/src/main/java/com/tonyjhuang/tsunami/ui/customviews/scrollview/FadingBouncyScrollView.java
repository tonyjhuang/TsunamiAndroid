package com.tonyjhuang.tsunami.ui.customviews.scrollview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tonyjhuang.tsunami.logging.Timber;

/**
 * Created by tony on 12/30/14.
 */
public class FadingBouncyScrollView extends BouncyScrollView {
    private static final float FADE_ALPHA = 0.25f;
    private static final int FADE_ANIM_DURATION = 200;

    private View customView;
    private ObjectAnimator alphaAnimator;

    public FadingBouncyScrollView(Context context) {
        super(context);
    }

    public FadingBouncyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingBouncyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCustomView(View customView, boolean animate, int duration) {
        super.setCustomView(customView, animate, duration);
        this.customView = customView;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (customView == null) return;
        customView.setAlpha(getAlphaForPosition(t));
    }

    private float getAlphaForPosition(int scrollY) {
        float alpha = (float) Math.pow(((float) scrollY) / getBottomScrollAssistThreshold(), 1.6f);
        return Math.min(alpha, 1f);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        if (isScrollable() && !isTouchingView(ev)) {
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
        if (isScrollable() && ((!isTouchingView(ev) && !draggingInside) || draggingOutside))
            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    fadeView(false);
            }
        return super.onTouchEvent(ev);
    }

    private void fadeView(boolean fade) {
        if (customView == null) return;
        float appropriateAlpha = getAlphaForPosition(getScrollY());
        float startAlpha = customView.getAlpha();
        float endAlpha = fade ? Math.min(FADE_ALPHA, appropriateAlpha) : appropriateAlpha;

        if (alphaAnimator != null && alphaAnimator.isRunning()) alphaAnimator.cancel();
        initAlphaAnimator(startAlpha, endAlpha).start();
    }

    private ObjectAnimator initAlphaAnimator(float startAlpha, float endAlpha) {
        alphaAnimator = ObjectAnimator.ofFloat(customView, "alpha", startAlpha, endAlpha)
                .setDuration(FADE_ANIM_DURATION);
        return alphaAnimator;
    }
}
