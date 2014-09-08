package com.tonyjhuang.tsunami.ui.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by tonyjhuang on 9/7/14.
 */
public class ContentCard extends Card {

    @InjectView(R.id.text)
    TextView textView;

    public ContentCard(Context context) {
        super(context, R.layout.card_content);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ButterKnife.inject(this, parent);
    }
}
