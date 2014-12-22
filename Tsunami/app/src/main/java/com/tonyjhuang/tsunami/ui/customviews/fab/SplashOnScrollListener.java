package com.tonyjhuang.tsunami.ui.customviews.fab;

import android.support.annotation.NonNull;

/**
 * Created by tonyjhuang on 11/4/14.
 */
public class SplashOnScrollListener extends ScrollDirectionDetector implements ScrollDirectionListener {
    private FloatingActionButton mFloatingActionButton;

    public SplashOnScrollListener() {
        setScrollDirectionListener(this);
    }

    public void setSplashButton(@NonNull FloatingActionButton floatingActionButton) {
        mFloatingActionButton = floatingActionButton;
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
        mFloatingActionButton.hide();
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
        mFloatingActionButton.show();
    }
}
