package com.tonyjhuang.tsunami.ui.main;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.utils.SimpleAnimatorListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class ContentScrollView extends ScrollView {
    @InjectView(R.id.container)
    LinearLayout container;
    @InjectView(R.id.content)
    CardView contentCardView;
   /* @InjectView(R.id.splash)
    EditText splashLayout;*/

    @InjectView(R.id.top_spacer)
    Space topSpacer;
    @InjectView(R.id.bottom_spacer)
    Space bottomSpacer;

    private float cardBottomPadding, cardTopPadding;
    private ContentCard contentCard;
    private SplashCard splashCard;

    /**
     * Is the splash card showing (not the content card)?
     */
    private boolean splashing = false;

    public ContentScrollView(Context context) {
        super(context);
        setup(context);
    }

    public ContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    private void setup(final Context context) {
        ButterKnife.inject(this, inflate(context, R.layout.view_content_scrollview, this));
        cardBottomPadding = getResources().getDimension(R.dimen.card_padding_bottom);
        cardTopPadding = getResources().getDimension(R.dimen.card_padding_top);
        contentCardView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Timber.d("i got a touch! " + motionEvent);
                return false;
            }
        });
    }


    private boolean flip = false;

    /**
     * Reset scroll position of the content card and have it slide up into the UI
     */
    public void resetContentCard() {
        animating = true;
        contentCardView.setVisibility(INVISIBLE);
        scrollTo(0, 0);
        post(new Runnable() {
            @Override
            public void run() {
                if (contentCard == null) {
                    contentCard = new ContentCard(getContext());
                    contentCardView.setCard(contentCard);
                }

                if (splashing) {
                    contentCardView.replaceCard(contentCard);
                    splashing = false;
                    //splashLayout.setVisibility(GONE);
                }

                if (flip) {
                    contentCard.setText(getContext().getString(R.string.lorem_ipsum_ext));
                } else {
                    contentCard.setText(getContext().getString(R.string.lorem_ipsum));
                }
                slideCardFromBottom.start();
                contentCardView.refreshCard(contentCard);
                contentCardView.setVisibility(VISIBLE);
                flip = !flip;
            }
        });

    }


    /**
     * Replace the content card with a splash card.
     */
    public void showSplashCard() {
        if (!splashing) {
            splashing = true;
            if (splashCard == null) {
                splashCard = new SplashCard(getContext());
            }

            contentCardView.replaceCard(splashCard);
            contentCardView.refreshCard(splashCard);
            // splashLayout.setVisibility(VISIBLE);
        }
    }

    public boolean isSplashCardShowing() {
        return splashing;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        /**
         * Only read the drag/scroll event if the motionevent is within bounds of our content card
         */
        int[] location = {0, 0};
        contentCardView.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + contentCardView.getWidth();
        int bottom = top + contentCardView.getHeight();

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

    private SimpleAnimatorListener animatorListener = new SimpleAnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animator) {
            animating = false;
        }
    };

    private Interpolator overshootInterpolator = new OvershootInterpolator();
    private ObjectAnimator slideCardFromBottom;
    private boolean animating = false;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (!animating) {
            if (t == 0) {
                // User swiped card down
                resetContentCard();
            } else if (t >= container.getHeight() - bottomSpacer.getHeight()) {
                // User swiped card up
                contentCardView.setVisibility(INVISIBLE);
                resetContentCard();
            }
        }
    }
}
