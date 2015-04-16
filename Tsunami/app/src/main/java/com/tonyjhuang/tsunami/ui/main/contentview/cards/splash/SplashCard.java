package com.tonyjhuang.tsunami.ui.main.contentview.cards.splash;

import android.content.Context;
import android.util.AttributeSet;

import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.content.ContentCard;

/**
 * Created by tonyjhuang on 9/14/14.
 */
public class SplashCard extends ContentCard {

    public SplashCard(Context context) {
        super(context);
    }

    public SplashCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SplashCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SplashContent retrieveSplashContent() {
        return new SplashContent(null, null, WaveContent.ContentType.text_content);
    }

    public void clear() {

    }
}
