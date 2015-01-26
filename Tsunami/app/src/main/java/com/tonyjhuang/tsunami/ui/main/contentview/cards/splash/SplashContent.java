package com.tonyjhuang.tsunami.ui.main.contentview.cards.splash;

import com.tonyjhuang.tsunami.api.models.WaveContent;

/**
 * Created by tony on 1/20/15.
 */
public class SplashContent {
    public String title;
    public String body;
    public WaveContent.ContentType contentType;

    public SplashContent(String title, String body, WaveContent.ContentType contentType) {
        this.title = title;
        this.body = body;
        this.contentType = contentType;
    }
}
