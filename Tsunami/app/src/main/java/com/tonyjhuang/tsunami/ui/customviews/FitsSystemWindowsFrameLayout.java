package com.tonyjhuang.tsunami.ui.customviews;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by tony on 1/15/15.
 */

public class FitsSystemWindowsFrameLayout extends FrameLayout {

    private Rect windowInsets = new Rect();
    private Rect tempInsets = new Rect();

    public FitsSystemWindowsFrameLayout(Context context) {
        super(context);
    }

    public FitsSystemWindowsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitsSystemWindowsFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        windowInsets.set(insets);
        super.fitSystemWindows(insets);
        return false;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        tempInsets.set(windowInsets);
        super.fitSystemWindows(tempInsets);
    }
}
