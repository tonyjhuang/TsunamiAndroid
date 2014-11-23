package com.tonyjhuang.tsunami.ui.main.wave.contentview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.button.SplashButton;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class WaveContentScrollView extends ScrollView implements WaveContentView {
    @InjectView(R.id.container)
    LinearLayout container;
    @InjectView(R.id.card_container)
    FrameLayout cardContainer;

    @InjectView(R.id.top_spacer)
    Space topSpacer;
    @InjectView(R.id.bottom_spacer)
    Space bottomSpacer;

    /**
     * Not actual padding, but we use this to increase the touch target of the
     * cards, which determines whether we want to scroll or not.
     */
    private float cardBottomPadding, cardTopPadding;

    /**
     * The card that contains information about the current wave.
     */
    private ContentCard contentCard;

    /**
     * The card that allows users to splash new waves.
     */
    private SplashCard splashCard;
    /**
     * Applied to our slideCardFromBottom animation, allows for a nice scroll detail
     */
    private Interpolator overshootInterpolator = new OvershootInterpolator();

    /**
     * Our simple animator for sliding the card up from the bottom
     */
    private ObjectAnimator slideCardFromBottom;

    private WavePresenter presenter;

    public WaveContentScrollView(Context context) {
        super(context);
        setup(context);
    }

    public WaveContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    private void setup(Context context) {
        ButterKnife.inject(this, inflate(context, R.layout.view_content_scrollview, this));
        cardBottomPadding = getResources().getDimension(R.dimen.card_padding_bottom);
        cardTopPadding = getResources().getDimension(R.dimen.card_padding_top);
        contentCard = new ContentCard(context);
    }

    @Override
    public void setPresenter(WavePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean isShowingSplashCard() {
        return splashCard != null
                && cardContainer.getChildAt(0) != null
                && cardContainer.getChildAt(0).equals(splashCard);
    }

    @Override
    public void showSplashCard() {
        if (isShowingSplashCard())
            return;

        if (splashCard == null)
            splashCard = new SplashCard(getContext());

        cardContainer.removeAllViews();
        cardContainer.addView(splashCard);
        animateCardView();
    }

    @Override
    public boolean isShowingContentCard() {
        return cardContainer.getChildAt(0) != null
                && cardContainer.getChildAt(0).equals(contentCard);
    }

    @Override
    public void showContentCard(Wave wave) {
        contentCard.setWave(wave);

        if (!isShowingContentCard()) {
            if (cardContainer.getChildAt(0) != null)
                cardContainer.removeAllViews();

            cardContainer.addView(contentCard);
        }
        animateCardView();
    }

    @Override
    public Wave getContentWave() {
        return contentCard.getWave();
    }

    /**
     * Simple Runnable to make the cardview visible and animate it to the set height.
     */
    Runnable animateRunnable = new Runnable() {
        @Override
        public void run() {
            slideCardFromBottom.start();
        }
    };

    /**
     * Animate the CardView if it isn't animating already.
     */
    public void animateCardView() {
        /**
         * Scroll to y-pos 1 as 0 triggers a swiped down event.
         */
        cardContainer.setVisibility(VISIBLE);
        post(animateRunnable);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        /**
         * Only read the drag/scroll event if the motionevent is within bounds of our content card
         */
        int[] location = {0, 0};
        cardContainer.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + cardContainer.getWidth();
        int bottom = top + cardContainer.getHeight();

        float x = ev.getX();
        float y = ev.getY();
        if ((x > left)
                && (x < right)
                && (y > top - cardTopPadding)
                && (y < bottom + cardBottomPadding)) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
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


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t == 0) {
            scrollTo(0, 1);
            if (isShowingContentCard()) {
                presenter.onContentSwipedDown();
            } else {
                presenter.onSplashSwipedDown();
            }
        } else if (t >= container.getHeight() - bottomSpacer.getHeight()) {
            scrollTo(0, 1);
            if (isShowingContentCard()) {
                presenter.onContentSwipedUp();
            } else {
                presenter.onSplashSwipedUp();
            }
        }

        if(splashButton != null) {
            if(t >= getHeight() - splashButtonLowerEdge) {
                splashButton.hide();
            } else {
                splashButton.show();
            }
        }
    }

    private final String STATE_SPLASHING = "splashing";

    @Override
    public Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean(STATE_SPLASHING, isShowingSplashCard());
        // ... save everything
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            if(bundle.getBoolean(STATE_SPLASHING)) {
                Timber.d("should be splashing!");
            }
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    private SplashButton splashButton;
    private int splashButtonLowerEdge = 0;

    @Override
    public void attachSplashButton(final SplashButton splashButton) {
        this.splashButton = splashButton;
        splashButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                splashButtonLowerEdge = splashButton.getTop() + splashButton.getHeight();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    splashButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    splashButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        Timber.d(String.format("top: %d, height: %d", splashButton.getTop(), splashButton.getHeight()));
    }
}
