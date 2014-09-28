package com.tonyjhuang.tsunami.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tonyjhuang.tsunami.utils.TypefaceManager;

/**
 * Created by tonyhuang on 9/21/14.
 */
public class FontAwesomeTextView extends TextView {

    public FontAwesomeTextView(Context context) {
        super(context);
        setup(context);
    }

    public FontAwesomeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public FontAwesomeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }

    private void setup(Context context) {
        setTypeface(TypefaceManager.get(context, "fontawesome.ttf"));
    }
}
