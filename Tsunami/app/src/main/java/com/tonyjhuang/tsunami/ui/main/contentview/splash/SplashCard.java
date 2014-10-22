package com.tonyjhuang.tsunami.ui.main.contentview.splash;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.tonyjhuang.tsunami.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tonyjhuang on 9/14/14.
 */
public class SplashCard extends CardView {
    @InjectView(R.id.content_container)
    FrameLayout container;

    public SplashCard(Context context) {
        super(context);
        setup(context);
    }

    public SplashCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public SplashCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    public void setup(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.card_splash, this, false);
        ButterKnife.inject(this, this);
    }

}
