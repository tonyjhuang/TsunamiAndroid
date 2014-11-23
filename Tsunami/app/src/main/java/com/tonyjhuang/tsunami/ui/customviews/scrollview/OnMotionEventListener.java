package com.tonyjhuang.tsunami.ui.customviews.scrollview;


import android.view.MotionEvent;

/**
 * Listen for MotionEvents.
 */
public interface OnMotionEventListener {
    public void onInterceptTouchEvent(MotionEvent ev);

    public void onTouchEvent(MotionEvent ev);
}