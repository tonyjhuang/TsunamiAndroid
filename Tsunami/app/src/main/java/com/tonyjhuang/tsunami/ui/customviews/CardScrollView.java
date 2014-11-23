package com.tonyjhuang.tsunami.ui.customviews;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.logging.Timber;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 11/11/14.
 */
public class CardScrollView extends ScrollView {
    private final static float FADED_ALPHA = 0.3f;

    @InjectView(R.id.container)
    protected LinearLayout container;
    @InjectView(R.id.card_container)
    protected FrameLayout cardContainer;

    @InjectView(R.id.top_spacer)
    protected Space topSpacer;
    @InjectView(R.id.bottom_spacer)
    protected Space bottomSpacer;

    private OnScrollChangedListener onScrollChangedListener;

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
    }

    /**
     * Set the view to display in our CardView and animate it from the bottom.
     *
     * @param cardView the new view to show.
     */
    public void setCardView(View cardView) {
        if(getCardView() == null || !getCardView().equals(cardView)) {
            cardContainer.removeAllViews();
            cardContainer.addView(cardView);
        }

        animateCardView();
    }

    public void setFadeCardView(boolean fadeCardView) {
        this.fadeCardView = fadeCardView;
        fadeOutAnimation = new AlphaAnimation(1.0f, FADED_ALPHA);
        fadeOutAnimation.setDuration(200);
        fadeOutAnimation.setFillAfter(true);
        fadeInAnimation = new AlphaAnimation(FADED_ALPHA, 1.0f);
        fadeInAnimation.setDuration(200);
        fadeInAnimation.setFillAfter(true);
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
     * @param faded should we fade the card view?
     */
    private void setCardViewFaded(boolean faded) {
        getCardView().clearAnimation();
        if(faded) {
            getCardView().startAnimation(fadeOutAnimation);
        } else {
            getCardView().startAnimation(fadeInAnimation);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Timber.d("action: " + ev.getAction());
        if(ev.getAction() == MotionEvent.ACTION_DOWN && !isTouchingCard(ev)) {
            draggingOutside = true;
            if(fadeCardView) {
                setCardViewFaded(true);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        Timber.d("action: " + ev.getAction());
        /**
         * Only read the drag/scroll event if the motionevent is within bounds of our content card
         */
        if(isTouchingCard(ev) && !draggingOutside) {
            return super.onTouchEvent(ev);
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    draggingOutside = false;
                    if(fadeCardView) {
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
        int[] location = {0, 0};
        cardContainer.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + cardContainer.getWidth();
        int bottom = top + cardContainer.getHeight();

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

            slideCardFromBottom = ObjectAnimator.ofInt(this, "scrollY", 1, getHeight() / 3);
            slideCardFromBottom.setInterpolator(overshootInterpolator);
            slideCardFromBottom.setDuration(500);

        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if(onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    /**
     * Listen to scroll events for this scroll view.
     */
    public interface OnScrollChangedListener {
        /**
         * Called whenever this ScrollView is scrolled.
         *
         * @param l    left
         * @param t    top
         * @param oldl old left
         * @param oldt old top
         */
        public void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
