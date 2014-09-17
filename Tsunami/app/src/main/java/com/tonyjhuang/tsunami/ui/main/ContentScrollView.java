package com.tonyjhuang.tsunami.ui.main;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.utils.SimpleAnimatorListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.view.CardView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class ContentScrollView extends ScrollView {
    @InjectView(R.id.container)
    LinearLayout container;
    @InjectView(R.id.content)
    CardView cardView;

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
     * Our interface class to ripple and retrieve no waves.
     */
    private WaveService waveService = null;

    /**
     * A fresh wave to display to the user
     */
    private Wave wave;

    /**
     * Is the splash card showing (not the content card)?
     */
    private boolean splashing = false;

    /**
     * Simple listener to reset isReset field.
     */
    private SimpleAnimatorListener animatorListener = new SimpleAnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animator) {
            isReset = false;
        }
    };

    /**
     * Applied to our slideCardFromBottom animation, allows for a nice scroll detail
     */
    private Interpolator overshootInterpolator = new OvershootInterpolator();

    /**
     * Our simple animator for sliding the card up from the bottom
     */
    private ObjectAnimator slideCardFromBottom;

    /**
     * Is the cardView currently sitting on the bottom of the scrollview?
     */
    private boolean isReset = false;

    public ContentScrollView(Context context) {
        super(context);
        setup(context);
    }

    public ContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    private void setup(Context context) {
        ButterKnife.inject(this, inflate(context, R.layout.view_content_scrollview, this));
        cardBottomPadding = getResources().getDimension(R.dimen.card_padding_bottom);
        cardTopPadding = getResources().getDimension(R.dimen.card_padding_top);
        contentCard = new ContentCard(context);
        cardView.setCard(contentCard);
    }

    /**
     * Sets the current wave service and automatically requests a new wave to display.
     */
    public void setWaveService(WaveService waveService) {
        this.waveService = waveService;
        requestNewWave();
    }

    /**
     * Reset scroll position of the current card.
     */
    public void resetCardView() {
        isReset = true;
        if (cardView != null) {
            cardView.setVisibility(GONE);
        }
        /**
         * Position the card beneath the user viewport.
         */
        scrollTo(0, 0);
    }

    /**
     * Simple Runnable to make the cardview visible and animate it to the set height.
     */
    Runnable animateRunnable = new Runnable() {
        @Override
        public void run() {
            cardView.setVisibility(VISIBLE);
            slideCardFromBottom.start();
        }
    };

    /**
     * Animate the CardView if it isn't animating already. Will only animate if
     * #resetCardView was called prior
     */
    public void animateCardView() {
        if (isReset) {
            cardView.setVisibility(VISIBLE);
            post(animateRunnable);
        }
    }

    /**
     * Hides and replaces the content card with the splash card and vice versa
     * DOES NOT ANIMATE THE CARD BACK IN. DO THAT YOURSELF.
     */
    public void setSplashing(boolean splashing) {
        cardView.setVisibility(INVISIBLE);
        if (splashing != this.splashing) {
            resetCardView();
            if (splashing) { // Show the splash card.
                if (splashCard == null) {
                    splashCard = new SplashCard(getContext());
                }

                cardView.replaceCard(splashCard);
                cardView.refreshCard(splashCard);
            } else {
                Timber.d("resetting card");
                cardView.replaceCard(contentCard);
                cardView.refreshCard(contentCard);
                //TODO: reset wave here to our cached wave.
            }
        }
        this.splashing = splashing;
    }

    /**
     * Display splash card if content card is showing and vice versa
     */
    public void toggleSplash() {
        setSplashing(!splashing);
        animateCardView();
    }

    /**
     * @param wave The fresh wave that we want to display to the user.
     */
    private void displayWave(Wave wave) {
        if (wave != null) {
            resetCardView();
            setSplashing(false);

            contentCard.setWave(wave);
            cardView.refreshCard(contentCard);
            animateCardView();
        }
    }

    /**
     * User has swiped the wave card down (wants to dismiss)
     */
    private void onWaveSwipedDown() {
        Timber.d("wave swiped down");
        resetCardView();
        if (splashing) {
            //TODO: show cached card
            requestNewWave();
        } else {
            requestNewWave();
        }
    }

    /**
     * User has swiped the wave card up (wants to ripple)
     */
    private void onWaveSwipedUp() {
        resetCardView();
        if (splashing) {
            rippleWave();
            //TODO: show cached card.
            requestNewWave();
        } else {
            notifyWaveServiceOfRipple();
            requestNewWave();
        }
    }

    /**
     * User has indicated that they want to ripple a new wave. Do validation and
     * sharing here.
     */
    private void rippleWave() {
        if (waveService != null) {
            waveService.rippleWave(null);
        }
    }

    /**
     * Ask our wave service (if we have one) for a new wave, then display it.
     */
    private void requestNewWave() {
        if (waveService != null) {
            waveService.getNextWave(new Callback<Wave>() {
                @Override
                public void success(Wave wave, Response response) {
                    displayWave(wave);
                }

                @Override
                public void failure(RetrofitError error) {
                    // TODO: failed to get wave?
                    Timber.d("ouch");
                }
            });
        }
    }

    /**
     * Passes a ripple event to our wave service if we have one
     */
    private void notifyWaveServiceOfRipple() {
        if (waveService != null) {
            waveService.rippleWave(wave);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        /**
         * Only read the drag/scroll event if the motionevent is within bounds of our content card
         */
        int[] location = {0, 0};
        cardView.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + cardView.getWidth();
        int bottom = top + cardView.getHeight();

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

            slideCardFromBottom = ObjectAnimator.ofInt(this, "scrollY", getHeight() / 3);
            slideCardFromBottom.setInterpolator(overshootInterpolator);
            slideCardFromBottom.setDuration(500);
            slideCardFromBottom.addListener(animatorListener);

        }
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (!isReset) {
            if (t == 0) {
                onWaveSwipedDown();
            } else if (t >= container.getHeight() - bottomSpacer.getHeight()) {
                onWaveSwipedUp();
            }
        }
    }

    public interface WaveService {
        /**
         * Retrieve the next wave to display to the user
         */
        public void getNextWave(Callback<Wave> callback);

        /**
         * Notify backend that the user has rippled this wave.
         */
        public void rippleWave(Wave wave);

        /**
         * User wants to splash a new wave.
         */
        public void splashWave();
    }
}
