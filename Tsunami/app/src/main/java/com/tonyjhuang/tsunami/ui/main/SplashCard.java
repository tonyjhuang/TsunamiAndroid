package com.tonyjhuang.tsunami.ui.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tonyjhuang.tsunami.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by tonyjhuang on 9/14/14.
 */
public class SplashCard extends Card {

   /* @InjectView(R.id.title)
    EditText title;*/

    public SplashCard(Context context) {
        super(context, R.layout.card_splash);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //ButterKnife.inject(this, parent);
        //title.setText("hello");
    }

}
