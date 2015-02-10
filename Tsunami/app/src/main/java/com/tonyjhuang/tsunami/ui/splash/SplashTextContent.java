package com.tonyjhuang.tsunami.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

/**
 * Created by tony on 2/10/15.
 */
public class SplashTextContent extends RelativeLayout implements SplashContentView{

    public SplashTextContent(Context context) {
        this(context, null);
    }

    public SplashTextContent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashTextContent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_splash_content_text, this);
    }

    @Override
    public void setFragment(TsunamiFragment fragment) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
