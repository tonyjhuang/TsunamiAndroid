package com.tonyjhuang.tsunami.ui.main.contentview.splash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tonyjhuang.tsunami.R;

import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by tonyjhuang on 9/14/14.
 */
public class SplashCard extends Card {
    @InjectView(R.id.content_container)
    FrameLayout container;



    public SplashCard(Context context) {
        super(context, R.layout.card_splash);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

    }

}
