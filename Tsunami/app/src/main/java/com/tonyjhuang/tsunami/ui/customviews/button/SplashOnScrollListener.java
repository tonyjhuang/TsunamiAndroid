package com.tonyjhuang.tsunami.ui.customviews.button;

import android.support.annotation.NonNull;

/**
 * Created by tonyjhuang on 11/4/14.
 */
public class SplashOnScrollListener extends ScrollDirectionDetector implements ScrollDirectionListener {
    private SplashButton mSplashButton;

    public SplashOnScrollListener() {
        setScrollDirectionListener(this);
    }

    public void setSplashButton(@NonNull SplashButton floatingActionButton) {
        mSplashButton = floatingActionButton;
    }

    /**
     * Called when the attached {@link android.widget.AbsListView} is scrolled down.
     * <br />
     * <br />
     * <i>Derived classes should call the super class's implementation of this method.
     * If they do not, the FAB will not react to AbsListView's scrolling events.</i>
     */
    @Override
    public void onScrollDown() {
        mSplashButton.hide();
    }

    /**
     * Called when the attached {@link android.widget.AbsListView} is scrolled up.
     * <br />
     * <br />
     * <i>Derived classes should call the super class's implementation of this method.
     * If they do not, the FAB will not react to AbsListView's scrolling events.</i>
     */
    @Override
    public void onScrollUp() {
        mSplashButton.show();
    }
}
