package com.tonyjhuang.tsunami.ui.main.contentview;

import com.tonyjhuang.tsunami.api.models.Wave;

/**
 * Created by tonyjhuang on 10/21/14.
 */
public interface ContentView {
    public void showSplashCard();

    public void showContentCard(Wave wave);

    public boolean isShowingSplashCard();

    public boolean isShowingContentCard();

    public Wave getContentWave();
}
