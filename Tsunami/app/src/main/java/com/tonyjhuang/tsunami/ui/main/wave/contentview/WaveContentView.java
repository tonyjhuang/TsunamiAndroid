package com.tonyjhuang.tsunami.ui.main.wave.contentview;

import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.main.button.SplashButton;
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

    public void setWaveContentViewScrollListener(WaveContentViewScrollListener scrollListener);
}
