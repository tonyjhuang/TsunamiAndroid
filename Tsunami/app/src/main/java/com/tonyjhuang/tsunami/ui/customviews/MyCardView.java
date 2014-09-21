package com.tonyjhuang.tsunami.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tonyjhuang.tsunami.logging.Timber;

import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by tonyjhuang on 9/14/14.
 */
public class MyCardView extends CardView {
    public MyCardView(Context context) {
        super(context);
    }

    public MyCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setClickable(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Timber.d("Have chance to intercept: will i? : " + super.onInterceptTouchEvent(ev));
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Timber.d("got an event here + " + event);
        return super.onTouchEvent(event);
    }
}
