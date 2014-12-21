package com.tonyjhuang.tsunami.ui.main.wave.contentview;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.CardScrollView;
import com.tonyjhuang.tsunami.ui.main.button.SplashButton;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class WaveContentScrollView extends CardScrollView implements WaveContentView {

    private final String STATE_SPLASHING = "splashing";

    /**
     * The card that contains information about the current wave.
     */
    private ContentCard contentCard;

    /**
     * The card that allows users to splash new waves.
     */
    private SplashCard splashCard;

    /**
     * Our view presenter.
     */
    private WavePresenter presenter;

    private OnScrollListener onScrollListener;
    private OnViewTypeChangedListener onViewTypeChangedListener;

    public WaveContentScrollView(Context context) {
        this(context, null);
    }

    public WaveContentScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveContentScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        contentCard = new ContentCard(context);
        setFadeCardView(true);
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

        if(onViewTypeChangedListener != null)
            onViewTypeChangedListener.onViewTypeChanged(ViewType.SPLASHING);

        setCardView(splashCard);
    }

    @Override
    public void showContentCard(Wave wave) {
        contentCard.setWave(wave);

        if (isShowingSplashCard()) {
            setCardView(contentCard);

            if(onViewTypeChangedListener != null)
                onViewTypeChangedListener.onViewTypeChanged(ViewType.CONTENT);
        } else {
            animateCardView();
        }
    }

    @Override
    public Wave getContentWave() {
        return contentCard.getWave();
    }

    int oldoldt, oldoldoldt;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        /**
         * Seems like theres a bug where the last scroll event gets repeated if overscroll is off.
         */
        if (t == oldoldt && oldt == oldoldoldt) {
            return;
        } else {
            oldoldt = t;
            oldoldoldt = oldt;
        }
        if (t == 0) {
            if (!isShowingSplashCard()) {
                presenter.onContentSwipedDown();
            } else {
                presenter.onSplashSwipedDown();
            }
        } else if (t >= getMaxScrollHeight()) {
            if (!isShowingSplashCard()) {
                presenter.onContentSwipedUp();
            } else {
                presenter.onSplashSwipedUp();
            }
        }

        if(onScrollListener != null)
            onScrollListener.onScroll(this, l, t, oldl, oldt);

        if (t < getCardViewStartingPosition()) {
            setCardAlpha((float) Math.pow(((float) t) / getCardViewStartingPosition(), 1.6f));
        } else {
            setCardAlpha(1.0f);
        }
    }

    @Override
    public SplashCard.SplashContent retrieveSplashContent() {
        return splashCard == null ? null : splashCard.retrieveSplashContent();
    }

    @Override
    public void clearSplashCard() {
        if (splashCard != null) {
            splashCard.clear();
        }
    }

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
            if (bundle.getBoolean(STATE_SPLASHING)) {
                Timber.d("should be splashing!");
            }
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public void setOnViewTypeChangedListener(OnViewTypeChangedListener onViewTypeChangedListener) {
        this.onViewTypeChangedListener = onViewTypeChangedListener;
    }
}
