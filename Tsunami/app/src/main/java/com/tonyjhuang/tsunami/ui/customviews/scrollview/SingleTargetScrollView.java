package com.tonyjhuang.tsunami.ui.customviews.scrollview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Space;

import com.tonyjhuang.tsunami.R;

/**
 * Created by tony on 1/30/15.
 */
public class SingleTargetScrollView extends ScrollView {

    private FrameLayout viewContainer;
    private Space topSpacer;
    private Space bottomSpacer;
    private View customView;

    protected boolean draggingOutside = false;
    protected boolean draggingInside = false;

    public SingleTargetScrollView(Context context) {
        this(context, null);
    }

    public SingleTargetScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleTargetScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_bouncy_scrollview, this);
        setOverScrollMode(OVER_SCROLL_NEVER);

        viewContainer = (FrameLayout) findViewById(R.id.view_container);
        topSpacer = (Space) findViewById(R.id.top_spacer);
        bottomSpacer = (Space) findViewById(R.id.bottom_spacer);
    }

    /* Blood & Guts */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            requestSpacerLayout();
        }
    }

    protected void requestSpacerLayout() {
        post(new Runnable() {
            @Override
            public void run() {
                setSpacerHeights(topSpacer, bottomSpacer);
            }
        });
    }

    protected void setSpacerHeights(Space topSpacer, Space bottomSpacer) {
    }

    /**
     * Let child Views consume the MotionEvent if they want to. Also, check if the user is pressing
     * and holding the area outside of our custom view bounds. If they are, then we can ignore future
     * onTouchEvents that land oa our custom view, as long as isDraggingOutside is true. If the user
     * quickly taps the screen, both ACTION_DOWN & _UP are passed to this method so take that into
     * account.
     */
    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        if (!isTouchingView(ev)) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    draggingOutside = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    draggingOutside = false;
                    break;
            }
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    draggingInside = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    draggingInside = false;
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        /**
         * Only consume the drag/scroll event if the MotionEvent is within the bounds of our view.
         */
        int action = ev.getAction();
        if (draggingInside || (isTouchingView(ev) && !draggingOutside)) {
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
                draggingInside = false;
            return super.onTouchEvent(ev);
        } else {
            switch (action) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    draggingInside = false;
                    draggingOutside = false;
            }
            return false;
        }
    }

    /**
     * Does this MotionEvent land on our CardView?
     */
    protected boolean isTouchingView(MotionEvent ev) {
        if (customView == null)
            return false;

        int[] location = {0, 0};
        customView.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + viewContainer.getWidth();
        int bottom = top + viewContainer.getHeight();

        float x = ev.getRawX();
        float y = ev.getRawY();
        return (x > left)
                && (x < right)
                && (y > top)
                && (y < bottom);
    }

    public View getCustomView() {
        return customView;
    }

    public void setCustomView(final View customView) {
        this.customView = customView;
        viewContainer.post(new Runnable() {
            @Override
            public void run() {
                viewContainer.removeAllViews();
                viewContainer.addView(customView);
                requestSpacerLayout();
            }
        });
    }

    protected View getCustomViewContainer() {
        return viewContainer;
    }
}
