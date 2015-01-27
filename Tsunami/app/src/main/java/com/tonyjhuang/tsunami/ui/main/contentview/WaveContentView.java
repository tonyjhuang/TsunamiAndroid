package com.tonyjhuang.tsunami.ui.main.contentview;

import android.view.View;

import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.main.WavePresenter;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.splash.SplashContent;

/**
 * Created by tonyjhuang on 10/21/14.
 */
public interface WaveContentView {

    /**
     * Set the view controller for this WaveContentView
     */
    public void setPresenter(WavePresenter presenter);

    /**
     * Set the current viewtype to SPLASHING and display the splash card to the user
     */
    public void showSplashCard();

    /**
     * Display a wave.
     */
    public void showContentCard(Wave wave);

    /**
     * Display a wave, animate the current view down the screen.
     */
    public void showContentCard(Wave wave, boolean animatePreviousViewDown);

    /**
     * Display a view that represents a lack of waves in the area.
     */
    public void showNoWavesCard();

    /**
     * Display a loading indicator;
     */
    public void showLoading();

    /**
     * Display an error.
     */
    public void showError();

    /**
     * Get the currently shown view.
     */
    public View getView();

    /**
     * What ViewType are we showing to the user right now?
     */
    public ViewType getCurrentViewType();

    /**
     * Get the current wave that we're showing. If this view is currently splashing, this method
     * should return null.
     */
    public Wave getContentWave();

    /**
     * Null out the current Wave.
     */
    public void clearContentWave();

    /**
     * Retrieve all user input from the splash card.
     */
    public SplashContent retrieveSplashContent();

    /**
     * Remove all user input from the splash card
     */
    public void clearSplashCard();

    /**
     * Scroll the cardview up offscreen.
     */
    public void scrollUpOffscreen();

    /**
     * Scroll the cardview down offscreen.
     */
    public void scrollDownOffscreen();

    /**
     * Hide the content view.
     */
    public void hideContent();

    /**
     * Set a custom OnScrollListener for this view that will be notified of scroll events.
     */
    public void setOnScrollListener(OnScrollListener onScrollListener);

    /**
     * Set a ViewTypeChangedListener for this view which will be notified when this view goes
     * from splashing to content and vice versa
     */
    public void setOnViewTypeChangedListener(OnViewTypeChangedListener onViewTypeChangedListener);

    public static interface OnScrollListener {
        public void onScroll(View view, int l, int t, int oldl, int oldt);
    }

    public static interface OnViewTypeChangedListener {
        public void onViewTypeChanged(ViewType viewType);
    }

    public static enum ViewType {
        CONTENT, SPLASHING, LOADING, NO_WAVES, ERROR, NONE
    }
}
