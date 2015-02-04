package com.tonyjhuang.tsunami.ui.customviews.scrollview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.Space;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.utils.SimpleAnimatorListener;

/**
 * Created by tony on 12/28/14.
 */
public class BouncyScrollView extends FadingSingleTargetScrollView {
    private ObjectAnimator viewAnimator;
    private Interpolator viewAnimationInterpolator = new OvershootInterpolator();
    private int viewAnimationDuration;

    private float relativeStartingPosition;
    private int absoluteStartingPosition;

    private boolean scrollable;

    private EventListener eventListener;
    private OnScrollStopListener onScrollStopListener = new OnScrollStopListener();

    /**
     * Once a certain percentage of the view is off the screen, should we automatically scroll it
     * entirely off? The threshold is the percentage of the view you want scrolled off before we
     * take control of it. Note: we will never auto-scroll the view as long as the user is touching
     * it.
     * <p>
     * Setting it to 0f is effectively turning off scrollAssist whereas setting it to 1f will scroll
     * the view as long as it touches the edge of the screen. Finally, 0.5f will scroll the view if
     * at least half of it is scrolled offscreen.
     */
    private boolean scrollAssist;
    private int scrollAssistDuration;
    private float scrollAssistThreshold;

    public BouncyScrollView(Context context) {
        this(context, null);
    }

    public BouncyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BouncyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // load the styled attributes and set their properties
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BouncyScrollView, defStyleAttr, 0);
        relativeStartingPosition = attributes.getFloat(R.styleable.BouncyScrollView_starting_position, 0.5f);
        viewAnimationDuration = attributes.getInteger(R.styleable.BouncyScrollView_anim_duration, 500);
        scrollAssist = attributes.getBoolean(R.styleable.BouncyScrollView_scroll_assist, false);
        scrollAssistDuration = attributes.getInteger(R.styleable.BouncyScrollView_scroll_assist_anim_duration, 200);
        scrollAssistThreshold = attributes.getDimension(R.styleable.BouncyScrollView_scroll_assist_threshold, 0);
        scrollable = attributes.getBoolean(R.styleable.BouncyScrollView_scrollable, true);
        attributes.recycle();
    }

    /* Blood & Guts */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setAbsoluteStartingPosition(relativeStartingPosition, h);
        initViewAnimator();
    }

    @Override
    protected void setSpacerHeights(Space topSpacer, Space bottomSpacer) {
        LinearLayout.LayoutParams topSpacerLayoutParams = (LinearLayout.LayoutParams) topSpacer.getLayoutParams();
        topSpacerLayoutParams.height = getHeight();
        topSpacer.setLayoutParams(topSpacerLayoutParams);

        LinearLayout.LayoutParams bottomSpacerLayoutParams = (LinearLayout.LayoutParams) bottomSpacer.getLayoutParams();
        bottomSpacerLayoutParams.height = getHeight();
        bottomSpacer.setLayoutParams(bottomSpacerLayoutParams);
    }

    @Override
    protected float getAlphaForPosition(int scrollY) {
        float alpha = (float) Math.pow(((float) scrollY) / getBottomScrollAssistThreshold(), 1.6f);
        return Math.min(alpha, 1f);
    }

    private void initViewAnimator() {
        viewAnimator = ObjectAnimator.ofInt(this, "scrollY", 1, absoluteStartingPosition);
        viewAnimator.setInterpolator(viewAnimationInterpolator);
        viewAnimator.setDuration(viewAnimationDuration);
        viewAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onScrollStopListener.setPause(false);
            }
        });
    }

    /**
     * Let child Views consume the MotionEvent if they want to. Also, check if the user is pressing
     * and holding the area outside of our custom view bounds. If they are, then we can ignore future
     * onTouchEvents that land on our custom view, as long as isDraggingOutside is true. If the user
     * quickly taps the screen, both ACTION_DOWN & _UP are passed to this method so take that into
     * account.
     */
    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        if (!scrollable) return false;
        onScrollStopListener.onInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (!scrollable) return false;
        onScrollStopListener.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    private int lastT, lastOldT;
    private int lastMaxScrollHeight;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //Timber.d("t: " + t + ", oldt: " + oldt);
        if (t == lastT && oldt == lastOldT)
            return;
        lastT = t;
        lastOldT = oldt;

        if (eventListener != null) eventListener.onScrollChanged(getCustomView(), l, t, oldl, oldt);

        if (t == 0) {
            if (eventListener != null) eventListener.onViewHitBottom(getCustomView());
        } else if (t == getMaxScrollHeight() && oldt != lastMaxScrollHeight) {
            lastMaxScrollHeight = getMaxScrollHeight();
            if (eventListener != null) eventListener.onViewHitTop(getCustomView());
        } else {
            onScrollStopListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    /* API */

    public static interface EventListener {
        public void onViewHitBottom(View view);

        public void onViewHitTop(View view);

        public void onScrollChanged(View view, int l, int t, int oldl, int oldt);
    }

    public void resetPosition() {
        post(new Runnable() {
            @Override
            public void run() {
                View viewContainer = getCustomViewContainer();
                viewContainer.setVisibility(INVISIBLE);
                viewContainer.scrollTo(0, 1);
                viewContainer.setVisibility(VISIBLE);
            }
        });
    }

    public void animateToStartingPosition() {
        onScrollStopListener.setPause(true);
        post(new Runnable() {
            @Override
            public void run() {
                if (viewAnimator != null) viewAnimator.start();
            }
        });
    }

    private int getMaxScrollHeight() {
        return getChildAt(0).getHeight() - getHeight();
    }

    public void setCustomView(final View customView) {
        setCustomView(customView, false);
    }

    public void setCustomView(final View customView, boolean animate) {
        setCustomView(customView, animate, 200);
    }

    public void setCustomView(final View customView, boolean animate, int duration) {
        if (getCustomView() == null || !animate) {
            resetPosition();
            super.setCustomView(customView);
            animateToStartingPosition();
        } else {
            onScrollStopListener.setPause(true);
            ObjectAnimator scrollDown = scrollToPosition(1, duration);
            scrollDown.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setCustomView(customView);
                }
            });
            scrollDown.start();
        }
    }

    /* Scroll Assist */

    /**
     * Check if our cardview is beyond the thresholds of readability (eg if either the top edge
     * of the CardView is near the bottom edge of the screen and vice versa). If so, scroll the
     * CardView entirely off screen.
     */
    private void scrollOffScreenIfNecessary() {
        if (!scrollAssist)
            return;
        int scrollY = getScrollY();
        //
        if (scrollY <= getBottomScrollAssistThreshold()) { // Should scroll down
            scrollDownOffscreen();
        } else if (scrollY >= getTopScrollAssistThreshold()) { // Should scroll up
            scrollUpOffscreen();
        } else {
            onScrollStopListener.setPause(false);
        }
    }

    protected float getBottomScrollAssistThreshold() {
        return scrollAssistThreshold;
    }

    protected float getTopScrollAssistThreshold() {
        return getMaxScrollHeight() - getBottomScrollAssistThreshold();
    }

    protected void scrollUpOffscreen() {
        scrollToPosition(getMaxScrollHeight(), scrollAssistDuration).start();
    }

    protected void scrollDownOffscreen() {
        scrollToPosition(0, scrollAssistDuration).start();
    }

    protected ObjectAnimator scrollToPosition(int position, int duration) {
        final ObjectAnimator animator = ObjectAnimator.ofInt(this, "scrollY", getScrollY(), position);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator());
        return animator;
    }

    /* Getters & Setters */

    public boolean isScrollAssist() {
        return scrollAssist;
    }

    public void setScrollAssist(boolean scrollAssist) {
        this.scrollAssist = scrollAssist;
    }

    public int getScrollAssistDuration() {
        return scrollAssistDuration;
    }

    public void setScrollAssistDuration(int scrollAssistDuration) {
        this.scrollAssistDuration = scrollAssistDuration;
    }

    public float getScrollAssistThreshold() {
        return scrollAssistThreshold;
    }

    public void setScrollAssistThreshold(int scrollAssistThreshold) {
        this.scrollAssistThreshold = scrollAssistThreshold;
    }

    public Interpolator getInterpolator() {
        return viewAnimationInterpolator;
    }

    public void setInterpolator(Interpolator viewAnimationInterpolator) {
        this.viewAnimationInterpolator = viewAnimationInterpolator;
        initViewAnimator();
    }

    public int getViewAnimationDuration() {
        return viewAnimationDuration;
    }

    public void setViewAnimationDuration(int viewAnimationDuration) {
        this.viewAnimationDuration = viewAnimationDuration;
        initViewAnimator();
    }

    public float getRelativeStartingPosition() {
        return relativeStartingPosition;
    }

    public void setRelativeStartingPosition(float relativeStartingPosition) {
        this.relativeStartingPosition = relativeStartingPosition;
        setAbsoluteStartingPosition(relativeStartingPosition, getHeight());
        initViewAnimator();
    }

    private void setAbsoluteStartingPosition(float relativeStartingPosition, int height) {
        absoluteStartingPosition = (int) ((1 - relativeStartingPosition) * height);
    }

    public float getAbsoluteStartingPosition() {
        return absoluteStartingPosition;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /* Utility */
    public class OnScrollStopListener {

        /**
         * How often we should check if the ScrollView has stopped scrolling in millis.
         * This Runnable is only started after the user has started scrolling.
         * 16 millis = 1 frame @ 60fps.
         */
        private static final int DELAY = 8;

        /**
         * The minimum difference between scroll events to be considered 'scrolling'
         */
        private static final int MINIMUM_VELOCITY = 32;

        /**
         * The y position we received last check.
         */
        private int oldY = 0;

        /**
         * Does the user currently have a finger down (are they scrolling)?
         */
        private boolean userFingerDown = false;

        /**
         * Stop operations! Why? I was getting issues with onScrollStop events being called
         * when setting a new custom view or animating the view to starting position. So that's why.
         */
        private boolean pause = false;

        /**
         * Runnable to compare the current to past scroll states.
         */
        private Runnable checkScrollView = new Runnable() {
            @Override
            public void run() {
                int y = getScrollY();
                if (Math.abs(y - oldY) <= MINIMUM_VELOCITY && !userFingerDown && !viewAnimator.isRunning()) {
                    onScrollStopped();
                } else {
                    oldY = y;
                    postDelayedRunnableCheck(DELAY);
                }
            }
        };

        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            oldY = oldt;
            postDelayedRunnableCheck(DELAY);
        }


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

        public void setPause(boolean pause) {
            this.pause = pause;
        }

        /**
         * Check for scroll stoppage again in the future.
         */
        private void postDelayedRunnableCheck(int delay) {
            removeCallbacks(checkScrollView);
            postDelayed(checkScrollView, delay);
        }

        private void onScrollStopped() {
            if (scrollAssist && !pause) {
                setPause(true);
                scrollOffScreenIfNecessary();
            }
        }
    }
}
