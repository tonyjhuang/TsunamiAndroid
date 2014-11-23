package com.tonyjhuang.tsunami.ui.customviews.scrollview;

/**
 * Listener for scroll events.
 *
 * Created by tony on 11/23/14.
 */
public interface OnScrollChangedListener {
    /**
     * Called whenever this ScrollView is scrolled.
     *
     * @param l    left
     * @param t    top
     * @param oldl old left
     * @param oldt old top
     */
    public void onScrollChanged(int l, int t, int oldl, int oldt);
}