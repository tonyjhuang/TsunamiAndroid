package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.tonyjhuang.tsunami.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tonyjhuang on 9/14/14.
 */
public class SplashCard extends FrameLayout {
    @InjectView(R.id.title)
    EditText title;
    @InjectView(R.id.body)
    EditText body;

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

    public SplashContent retrieveSplashContent() {
        return new SplashContent(title.getText().toString(), body.getText().toString());
    }

    public void clear() {
        title.setText("");
        body.setText("");
    }

    public static class SplashContent {
        public String title;
        public String body;

        public SplashContent(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }
}
