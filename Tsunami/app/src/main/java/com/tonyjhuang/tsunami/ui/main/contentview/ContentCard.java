package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by tonyjhuang on 9/7/14.
 */
public class ContentCard extends Card {

    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.text)
    TextView textView;

    public ContentCard(Context context) {
        super(context, R.layout.card_content);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ButterKnife.inject(this, parent);
    }

    public void setWave(Wave wave) {
        title.setText(wave.getTitle());
        textView.setText(wave.getMessage());
    }

    private String firstFiveWordsOf(String string) {
        int i = nthOccurrence(string, ' ', 4);
        return string.substring(0, i);
    }

    public int nthOccurrence(String str, char c, int n) {
        int pos = str.indexOf(c, 0);
        while (n-- > 0 && pos != -1)
            pos = str.indexOf(c, pos + 1);
        return pos;
    }
}
