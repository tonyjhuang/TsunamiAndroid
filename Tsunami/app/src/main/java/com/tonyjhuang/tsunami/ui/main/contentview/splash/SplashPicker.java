package com.tonyjhuang.tsunami.ui.main.contentview.splash;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tonyjhuang.tsunami.R;

/**
 * Created by tonyhuang on 9/21/14.
 */
public class SplashPicker extends LinearLayout {

    public SplashPicker(Context context) {
        super(context);
        setup(context);
    }

    public SplashPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public SplashPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }

    private void setup(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_splash_picker, this, true);
    }
}
