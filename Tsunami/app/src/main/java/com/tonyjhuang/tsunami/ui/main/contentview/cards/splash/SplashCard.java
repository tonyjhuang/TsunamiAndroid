package com.tonyjhuang.tsunami.ui.main.contentview.cards.splash;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.TsunamiCard;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tonyjhuang on 9/14/14.
 */
public class SplashCard extends TsunamiCard {
    @InjectView(R.id.title)
    EditText title;
    @InjectView(R.id.body)
    EditText body;

    public SplashCard(Context context) {
         super(context);
    }

    public SplashCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SplashCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View getInnerView(Context context, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_splash, container, false);
        ButterKnife.inject(this, view);
        return view;
    }


    public SplashContent retrieveSplashContent() {
        return new SplashContent(title.getText().toString(),
                body.getText().toString(),
                WaveContent.ContentType.text);
    }

    public void clear() {
        title.setText("");
        body.setText("");
    }
}
