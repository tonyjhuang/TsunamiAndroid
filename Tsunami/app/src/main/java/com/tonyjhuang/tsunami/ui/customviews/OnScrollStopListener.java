package com.tonyjhuang.tsunami.ui.customviews;

import android.view.MotionEvent;
import android.widget.ScrollView;

import com.tonyjhuang.tsunami.ui.customviews.scrollview.CardScrollView;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.OnMotionEventListener;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.OnScrollListener;

/**
 * Created by tony on 11/23/14.
 */
public abstract class OnScrollStopListener implements
        OnMotionEventListener,
        OnScrollListener {

    /**
     * How often we should check if the ScrollView has stopped scrolling. See Runnable checkScrollView.
     * This Runnable is only started after the user has started scrolling.
     */
    private static final int DELAY = 50;

    /**
     * The ScrollView we're listening to.
     */
    private ScrollView scrollView;

    /**
     * The y position we received last check.
     */
    private int oldY = 0;

    /**
     * Runnable to compare the current to past scroll states.
     */
    private Runnable checkScrollView = new Runnable() {
        @Override
        public void run() {
            int currentY = scrollView.getScrollY();
            if (currentY == oldY && !userFingerDown) {
                onScrollStopped();
            } else {
                oldY = currentY;
                postDelayedRunnableCheck(DELAY);
            }
        }
    };

    /**
     * Does the user currently have a finger down (are they scrolling)?
     */
    private boolean userFingerDown = false;

    /**
     * For now we only accept CardScrollViews. However, we should consider creating a listenable
     * interface to make this more useful in future cases.
     */
    public void attach(CardScrollView scrollView) {
        this.scrollView = scrollView;
        scrollView.setOnMotionEventListener(this);
        scrollView.setOnScrollListener(this);
    }

    @Override
    public void onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                userFingerDown = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                userFingerDown = false;
                postDelayedRunnableCheck(0);
                break;
        }
    }

    @Override
    public void onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                userFingerDown = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                userFingerDown = false;
                postDelayedRunnableCheck(0);
                break;
        }
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        oldY = oldt;
        postDelayedRunnableCheck(DELAY);
    }

    /**
     * Check for scroll stoppage again in the future.
     */
    private void postDelayedRunnableCheck(int delay) {
        scrollView.removeCallbacks(checkScrollView);
        scrollView.postDelayed(checkScrollView, delay);
    }

    /**
     * Called when our scrollView has stopped scrolling/being scrolled
     */
    public abstract void onScrollStopped();
}
