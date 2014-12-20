package com.tonyjhuang.tsunami.ui.main.wave.contentview;

import android.view.View;

import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.main.wave.WavePresenter;

/**
 * Created by tonyjhuang on 10/21/14.
 */
public interface WaveContentView {

    public void setPresenter(WavePresenter presenter);

    public void showSplashCard();

    public void showContentCard(Wave wave);

    public boolean isShowingSplashCard();

    public boolean isShowingContentCard();

    public Wave getContentWave();

    public SplashCard.SplashContent retrieveSplashContent();

    public void clearSplashCard();

    public void setOnScrollListener(OnScrollListener onScrollListener);

    public void setOnViewTypeChangedListener(OnViewTypeChangedListener onViewTypeChangedListener);

    public static interface OnScrollListener {
        public void onScroll(View view, int l, int t, int oldl, int oldt);
    }

    public static interface OnViewTypeChangedListener {
        public void onViewTypeChanged(ViewType viewType);
    }

    public static enum ViewType {
        CONTENT, SPLASHING
    }
}
