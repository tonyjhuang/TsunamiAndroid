package com.tonyjhuang.tsunami.ui.customviews.scrollview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.tonyjhuang.tsunami.logging.Timber;

/**
 * Created by tony on 12/25/14.
 */
public class ObservableParallaxScrollView extends ParallaxScrollView {
    private OnScrollListener onScrollListener;

    public ObservableParallaxScrollView(Context context) {
        this(context, null);
    }

    public ObservableParallaxScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ObservableParallaxScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16)
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);

                onScrollChanged(getScrollX(), getScrollY(), 0, 0);
            }
        });
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
