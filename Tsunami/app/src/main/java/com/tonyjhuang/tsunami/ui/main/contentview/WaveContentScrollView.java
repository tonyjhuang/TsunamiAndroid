package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.BouncyScrollView;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.content.ContentCard;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.splash.SplashCard;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.splash.SplashContent;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.status.ErrorCard;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.status.NoWavesCard;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class WaveContentScrollView extends BouncyScrollView implements
        WaveContentView,
        BouncyScrollView.EventListener {

    /**
     * The card that contains information about the current wave.
     */
    private ContentCard contentCard;

    /**
     * The card that allows users to splash new waves.
     */
    private SplashCard splashCard;

    /**
     * The card that tells the user there are no waves in the area.
     */
    private NoWavesCard noWavesCard;

    /**
     * The card to notify user of an error getting waves.
     */
    private ErrorCard errorCard;

    /**
     * Our view presenter.
     */
    private WavePresenter presenter;

    /**
     * State listeners.
     */
    private OnScrollListener onScrollListener;
    private OnViewTypeChangedListener onViewTypeChangedListener;

    /**
     * View we show to indicate loading.
     */
    private ProgressBar loadingView;

    /**
     * What ViewType are we showing to the user?
     */
    private ViewType currentViewType = ViewType.NONE;


    public WaveContentScrollView(Context context) {
        this(context, null);
    }

    public WaveContentScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveContentScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        contentCard = new ContentCard(context);
        setEventListener(this);
        loadingView = new ProgressBar(context);
    }

    @Override
    public void setPresenter(WavePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void disableInteractions(boolean disable) {
        setScrollable(!disable);
    }

    @Override
    public void setOnViewTypeChangedListener(OnViewTypeChangedListener onViewTypeChangedListener) {
        this.onViewTypeChangedListener = onViewTypeChangedListener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public void showLoading() {
        if (currentViewType.equals(ViewType.LOADING)) return;

        if (onViewTypeChangedListener != null)
            onViewTypeChangedListener.onViewTypeChanged(ViewType.LOADING);

        setScrollable(false);
        setCustomView(loadingView);

        currentViewType = ViewType.LOADING;
    }

    @Override
    public void showContent(Wave wave) {
        showContent(wave, true);
    }

    @Override
    public void showContent(Wave wave, boolean animatePreviousViewDown) {
        if (!currentViewType.equals(ViewType.CONTENT)) {
            if (onViewTypeChangedListener != null)
                onViewTypeChangedListener.onViewTypeChanged(ViewType.CONTENT);

            setCustomView(contentCard, animatePreviousViewDown);
        } else {
            animateToStartingPosition();
        }
        setScrollable(true);
        contentCard.setWave(wave);

        currentViewType = ViewType.CONTENT;
    }

    @Override
    public Wave getContentWave() {
        return contentCard.getWave();
    }

    @Override
    public void clearContentWave() {
        contentCard.setWave(null);
    }

    @Override
    public void showSplash() {
        if (currentViewType.equals(ViewType.SPLASHING)) return;

        if (onViewTypeChangedListener != null)
            onViewTypeChangedListener.onViewTypeChanged(ViewType.SPLASHING);

        if (splashCard == null) splashCard = new SplashCard(getContext());

        setScrollable(true);
        setCustomView(splashCard, true);

        currentViewType = ViewType.SPLASHING;
    }

    @Override
    public SplashContent retrieveSplashContent() {
        return splashCard == null ? null : splashCard.retrieveSplashContent();
    }

    @Override
    public void clearSplashCard() {
        if (splashCard != null) {
            splashCard.clear();
        }
    }

    @Override
    public void showNoWaves() {
        if (currentViewType.equals(ViewType.NO_WAVES)) return;

        if (onViewTypeChangedListener != null)
            onViewTypeChangedListener.onViewTypeChanged(ViewType.NO_WAVES);

        if (noWavesCard == null) noWavesCard = new NoWavesCard(getContext());

        setScrollable(true);
        setCustomView(noWavesCard, true);

        currentViewType = ViewType.NO_WAVES;
    }

    @Override
    public void showError() {
        if (currentViewType.equals(ViewType.ERROR)) return;

        if (onViewTypeChangedListener != null)
            onViewTypeChangedListener.onViewTypeChanged(ViewType.ERROR);

        if (errorCard == null) errorCard = new ErrorCard(getContext());

        setScrollable(true);
        setCustomView(errorCard, true);

        currentViewType = ViewType.ERROR;
    }

    @Override
    public ViewType getCurrentViewType() {
        return currentViewType;
    }

    @Override
    public View getView() {
        return getCustomView();
    }

    @Override
    public void scrollUpOffscreen() {
        super.scrollUpOffscreen();
    }

    @Override
    public void scrollDownOffscreen() {
        super.scrollDownOffscreen();
    }

    @Override
    public void hideContent() {
        resetPosition();
    }

    @Override
    public void onViewHitBottom(View view) {
        if (presenter == null) return;
        switch (currentViewType) {
            case SPLASHING:
                presenter.onSplashSwipedDown();
                break;
            case CONTENT:
                presenter.onContentSwipedDown();
                break;
            case NO_WAVES:
                presenter.onNoWavesSwipedDown();
                break;
            case ERROR:
                presenter.onErrorSwipedDown();
                break;
        }
    }

    @Override
    public void onViewHitTop(View view) {
        if (presenter == null) return;
        switch (currentViewType) {
            case SPLASHING:
                presenter.onSplashSwipedUp();
                break;
            case CONTENT:
                presenter.onContentSwipedUp();
                break;
            case NO_WAVES:
                presenter.onNoWavesSwipedUp();
                break;
            case ERROR:
                presenter.onErrorSwipedUp();
                break;
        }
    }

    @Override
    public void onScrollChanged(View view, int l, int t, int oldl, int oldt) {
        if (onScrollListener != null) onScrollListener.onScroll(view, l, t, oldl, oldt);
    }

}
