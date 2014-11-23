package com.tonyjhuang.tsunami.ui.main.wave.contentview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.CardScrollView;
import com.tonyjhuang.tsunami.ui.main.button.SplashButton;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class WaveContentScrollView extends CardScrollView implements WaveContentView {
    /**
     * The card that contains information about the current wave.
     */
    private ContentCard contentCard;

    /**
     * The card that allows users to splash new waves.
     */
    private SplashCard splashCard;

    private WavePresenter presenter;

    /**
     * Reference to our splash button, that floats in the upper right corner.
     */
    private SplashButton splashButton;

    /**
     * The lowest point of our button.
     */
    private int splashButtonLowerEdge = 0;

    public WaveContentScrollView(Context context) {
        this(context, null);
    }

    public WaveContentScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveContentScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        contentCard = new ContentCard(context);
    }

    @Override
    public void setPresenter(WavePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean isShowingSplashCard() {
        return splashCard != null
                && getCardView() != null
                && getCardView().equals(splashCard);
    }

    @Override
    public void showSplashCard() {
        if (isShowingSplashCard())
            return;

        if (splashCard == null)
            splashCard = new SplashCard(getContext());

        setCardView(splashCard);
    }

    @Override
    public boolean isShowingContentCard() {
        return contentCard != null
                && getCardView() != null
                && getCardView().equals(contentCard);
    }

    @Override
    public void showContentCard(Wave wave) {
        contentCard.setWave(wave);

        if (!isShowingContentCard()) {
            setCardView(contentCard);
        } else {
            animateCardView();
        }
    }

    @Override
    public Wave getContentWave() {
        return contentCard.getWave();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
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

        if (splashButton != null) {
            /**
             * If our card is touching or above the lower edge of our splash button, hide it.
             */
            if (t >= getHeight() - splashButtonLowerEdge) {
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
