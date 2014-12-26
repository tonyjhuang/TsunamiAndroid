package com.tonyjhuang.tsunami.ui.customviews.scrollview;

import android.content.Context;
import android.util.AttributeSet;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;

/**
 * Created by tony on 12/25/14.
 */
public class ObservableParallaxScrollView extends ParallaxScrollView {
    private OnScrollListener onScrollListener;

    public ObservableParallaxScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableParallaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableParallaxScrollView(Context context) {
        super(context);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null)
            this.onScrollListener.onScrollChanged(l, t, oldl, oldt);
    }
}
