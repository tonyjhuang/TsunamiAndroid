package com.tonyjhuang.tsunami.ui.main.wave.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.tonyjhuang.tsunami.R;

import butterknife.ButterKnife;

/**
 * Created by tonyjhuang on 9/14/14.
 */
public class SplashCard extends FrameLayout {

    public SplashCard(Context context) {
        this(context, null);
    }

    public SplashCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    public void setup(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ButterKnife.inject(this, inflater.inflate(R.layout.card_splash, this, true));
    }
}
