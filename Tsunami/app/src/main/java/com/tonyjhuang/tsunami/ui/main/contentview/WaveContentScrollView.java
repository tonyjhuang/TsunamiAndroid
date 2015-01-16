package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.BouncyScrollView;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.FadingBouncyScrollView;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class WaveContentScrollView extends FadingBouncyScrollView implements
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
     * Are we currently showing the user the splash card?
     */
    private boolean splashing = false;

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
    public void setOnViewTypeChangedListener(OnViewTypeChangedListener onViewTypeChangedListener) {
        this.onViewTypeChangedListener = onViewTypeChangedListener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public void showLoading() {
        if (splashing && onViewTypeChangedListener != null)
            onViewTypeChangedListener.onViewTypeChanged(ViewType.CONTENT);
        splashing = false;
        setScrollable(false);
        setCustomView(loadingView);
    }

    @Override
    public void showContentCard(Wave wave) {
        showContentCard(wave, false);
    }

    @Override
    public void showContentCard(Wave wave, boolean postSuccessfulSplash) {
        setScrollable(true);
        contentCard.setWave(wave);

        if (splashing && onViewTypeChangedListener != null)
            onViewTypeChangedListener.onViewTypeChanged(ViewType.CONTENT);
        splashing = false;

        if (wave != null) {
            if (getCustomView() != contentCard)
                setCustomView(contentCard, !postSuccessfulSplash);
            else
                animateToStartingPosition();
        }
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
    public void showSplashCard() {
        if (splashing) return;
        splashing = true;
        setScrollable(true);

        if (splashCard == null)
            splashCard = new SplashCard(getContext());

        if (onViewTypeChangedListener != null)
            onViewTypeChangedListener.onViewTypeChanged(ViewType.SPLASHING);

        setCustomView(splashCard, true);
    }

    @Override
    public boolean isShowingSplashCard() {
        return splashing;
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
        if (splashing)
            presenter.onSplashSwipedDown();
        else
            presenter.onContentSwipedDown();
    }

    @Override
    public void onViewHitTop(View view) {
        if (presenter == null) return;
        if (splashing)
            presenter.onSplashSwipedUp();
        else
            presenter.onContentSwipedUp();
    }

    @Override
    public void onScrollChanged(View view, int l, int t, int oldl, int oldt) {
        if (onScrollListener != null) onScrollListener.onScroll(view, l, t, oldl, oldt);
    }

}
