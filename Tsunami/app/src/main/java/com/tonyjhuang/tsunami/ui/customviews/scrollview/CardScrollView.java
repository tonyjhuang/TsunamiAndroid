package com.tonyjhuang.tsunami.ui.customviews.scrollview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.OnScrollStopListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 11/11/14.
 */
public class CardScrollView extends ObservableScrollView {
    private final static float FADED_ALPHA = 0.3f;

    @InjectView(R.id.container)
    protected LinearLayout container;
    @InjectView(R.id.card_container)
    protected FrameLayout cardContainer;

    @InjectView(R.id.top_spacer)
    protected Space topSpacer;
    @InjectView(R.id.bottom_spacer)
    protected Space bottomSpacer;

    /**
     * Listener for MotionEvents.
     */
    private OnMotionEventListener onMotionEventListener;

    /**
     * Not actual padding, but we use this to increase the touch target of the
     * cards, which determines whether we want to scroll or not.
     */
    private float cardBottomPadding, cardTopPadding;

    /**
     * Our simple animator for sliding the card up from the bottom
     */
    private ObjectAnimator slideCardFromBottom;

    /**
     * Applied to our slideCardFromBottom animation, gives us a nice scroll detail
     */
    private Interpolator overshootInterpolator = new OvershootInterpolator();

    /**
     * Simple reusable Runnable to make the card animate to the starting position.
     */
    Runnable animateRunnable = new Runnable() {
        @Override
        public void run() {
            slideCardFromBottom.start();
        }
    };

    /**
     * Do we lower our CardView's alpha on touch outside of the CardView?
     */
    private boolean fadeCardView = false;

    /**
     * Animations to use for fading in and our our VardCiew
     */
    private AlphaAnimation fadeOutAnimation, fadeInAnimation;

    /**
     * In order to make our animations for versatile, we keep track of what the original alpha
     * of our CardView is before fading it out so when we fade back in, we can preserve its initial
     * state.
     */
    private float previousFullAlpha = 1.0f;

    /**
     * Is the user currently touching or dragging the area outside of our CardView?
     */
    private boolean draggingOutside = false;

    public CardScrollView(Context context) {
        this(context, null);
    }

    public CardScrollView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CardScrollView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        ButterKnife.inject(this, inflate(context, R.layout.view_card_scrollview, this));
        cardBottomPadding = getResources().getDimension(R.dimen.card_padding_bottom);
        cardTopPadding = getResources().getDimension(R.dimen.card_padding_top);

        setOverScrollMode(OVER_SCROLL_NEVER);

        new OnScrollStopListener() {
            @Override
            public void onScrollStopped() {
                scrollOffScreenIfNecessary();
            }
        }.attach(this);
    }

    /**
     * Set the view to display in our CardView and animate it from the bottom.
     *
     * @param cardView the new view to show.
     */
    public void setCardView(View cardView) {
        if (getCardView() == null || !getCardView().equals(cardView)) {
            cardContainer.removeAllViews();
            cardContainer.addView(cardView);
        }

        animateCardView();
    }

    /**
     * Should we fade the CardView when the user taps/holds outside of the scrollview?
     */
    public void setFadeCardView(boolean fadeCardView) {
        this.fadeCardView = fadeCardView;
    }

    /**
     * Get the view that is currently being displayed. Can be null if the cardview has never been set.
     *
     * @return the view currently shown in our cardview.
     */
    public View getCardView() {
        return cardContainer.getChildAt(0);
    }

    /**
     * Animate the CardView if it isn't animating already.
     */
    public void animateCardView() {
        cardContainer.scrollTo(0, 1);
        /**
         * Scroll to y-pos 1 as 0 triggers a swiped down event.
         */
        cardContainer.setVisibility(VISIBLE);
        post(animateRunnable);
    }

    /**
     * Set this CardViews alpha value.
     */
    protected void setCardAlpha(float alpha) {
        if (getCardView() != null)
            getCardView().setAlpha(alpha);
    }

    /**
     * @param faded should we fade the card view?
     */
    private void setCardViewFaded(boolean faded) {
        if (getCardView() == null)
            return;

        getCardView().clearAnimation();
        if (faded) {
            previousFullAlpha = getCardView().getAlpha();
            fadeOutAnimation = new AlphaAnimation(previousFullAlpha, FADED_ALPHA);
            fadeOutAnimation.setDuration(200);
            fadeOutAnimation.setFillAfter(true);
            getCardView().startAnimation(fadeOutAnimation);
        } else {
            if (FADED_ALPHA < previousFullAlpha) {
                fadeInAnimation = new AlphaAnimation(FADED_ALPHA, previousFullAlpha);
                fadeInAnimation.setDuration(200);
                fadeInAnimation.setFillAfter(true);
                getCardView().startAnimation(fadeInAnimation);
            }
        }
    }

    /**
     * Set the listener for MotionEvents for this CardScrollView
     */
    public void setOnMotionEventListener(OnMotionEventListener onMotionEventListener) {
        this.onMotionEventListener = onMotionEventListener;
    }

    /**
     * The reason why we care about both #onInterceptTouchEvent and #onTouchEvent is because we want
     * to allow child views to consume the MotionEvent before we try to, so we have to
     * implement scrolling logic in #onTouchEvent. However, ACTION_DOWN is only passed to ViewGroups'
     * #onInterceptTouchEvent, so we need to add some logic to cover the case where a user taps
     * the screen and immediately lifts up their finger, in which case both ACTION_DOWN and ACTION_UP
     * are passed #onInterceptTouchEvent.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (onMotionEventListener != null) {
            onMotionEventListener.onInterceptTouchEvent(ev);
        }

        if (!isTouchingCard(ev)) {
            /**
             * We really only care about the MotionEvent if the user isn't touching the card view.
             */
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    draggingOutside = true;
                    if (fadeCardView) {
                        setCardViewFaded(true);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    draggingOutside = false;
                    if (fadeCardView) {
                        setCardViewFaded(false);
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (onMotionEventListener != null) {
            onMotionEventListener.onTouchEvent(ev);
        }

        /**
         * Only read the drag/scroll event if the motionevent is within bounds of our content card
         */
        if (isTouchingCard(ev) && !draggingOutside) {
            return super.onTouchEvent(ev);
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    draggingOutside = false;
                    if (fadeCardView) {
                        setCardViewFaded(false);
                    }
            }
            return false;
        }
    }

    /**
     * Does this MotionEvent land on our CardView?
     */
    private boolean isTouchingCard(MotionEvent ev) {
        if (getCardView() == null)
            return false;

        int[] location = {0, 0};
        getCardView().getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + getCardView().getWidth();
        int bottom = top + getCardView().getHeight();

        float x = ev.getX();
        float y = ev.getY();
        return (x > left)
                && (x < right)
                && (y > top - cardTopPadding)
                && (y < bottom + cardBottomPadding);
    }

    /**
     * @param changed layout bounds changed
     * @param l       left
     * @param t       top
     * @param r       right
     * @param b       bottom
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            /**
             * Set the height of the top and bottom spacers to the same size as this container so
             * the card has space to scroll out of screen.
             */
            LinearLayout.LayoutParams topSpacerLayoutParams = (LinearLayout.LayoutParams) topSpacer.getLayoutParams();
            topSpacerLayoutParams.height = getHeight();
            topSpacer.setLayoutParams(topSpacerLayoutParams);

            LinearLayout.LayoutParams bottomSpacerLayoutParams = (LinearLayout.LayoutParams) bottomSpacer.getLayoutParams();
            bottomSpacerLayoutParams.height = getHeight();
            bottomSpacer.setLayoutParams(bottomSpacerLayoutParams);

            slideCardFromBottom = ObjectAnimator.ofInt(this, "scrollY", 1, getCardViewStartingPosition());
            slideCardFromBottom.setInterpolator(overshootInterpolator);
            slideCardFromBottom.setDuration(500);
        }
    }

    /**
     * Check if our cardview is beyond the thresholds of readability (eg if either the top edge
     * of the CardView is near the bottom edge of the screen and vice versa). If so, scroll the
     * CardView entirely off screen.
     */
    private void scrollOffScreenIfNecessary() {
        int scrollY = getScrollY();

        if (scrollY <= getScrollOutBottomThreshold()) {
            /**
             * Top of the CardView is sitting on the bottom edge of the screen
             */
            Timber.d("Should scroll CardView down.");
            scrollDownOffscreen();
        } else if (scrollY >= getScrollOutTopThreshold()) {
            /**
             * Bottom of the CardView is sitting on the upper edge of the screen
             */
            Timber.d("Should scroll CardView up.");
            scrollUpOffscreen();
        }
    }

    protected void scrollUpOffscreen() {
        scrollToPosition(getMaxScrollHeight(), 200);
    }

    protected void scrollDownOffscreen() {
        scrollToPosition(0, 200);
    }

    protected void scrollToPosition(int position, int duration) {
        int scrollY = getScrollY();
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "scrollY", scrollY, position);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator());
        animate(animator);
    }

    private void animate(final ObjectAnimator animator) {
        post(animator::start);
    }

    /**
     * @return the total height of the children for this scrollview.
     */
    protected int getTotalHeight() {
        return bottomSpacer.getHeight() + (getCardView() == null ? 0 : getCardView().getHeight()) + topSpacer.getHeight();
    }

    /**
     * @return the highest scroll y value possible for this scrollview
     */
    protected int getMaxScrollHeight() {
        return getTotalHeight() - bottomSpacer.getHeight();
    }

    protected int getCardViewStartingPosition() {
        return getHeight() / 3;
    }

    /**
     * The minimum scroll threshold for y position before we scroll the card downwards.
     */
    protected int getScrollOutBottomThreshold() {
        return (int) getResources().getDimension(R.dimen.card_scroll_view_readability_threshold);
    }

    /**
     * The maximum scroll threshold for y position before we scroll the card upwards.
     */
    protected int getScrollOutTopThreshold() {
        int threshold = (int) getResources().getDimension(R.dimen.card_scroll_view_readability_threshold);
        int cardViewHeight = getCardView() == null ? 0 : getCardView().getHeight();
        int screenHeight = getHeight();
        return screenHeight + cardViewHeight - threshold;
    }
}