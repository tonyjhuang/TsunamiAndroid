package com.tonyjhuang.tsunami.ui.main.contentview.cards.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 1/20/15.
 */
public class ContentInnerText extends LinearLayout implements ContentInnerView {

    @InjectView(R.id.caption)
    TextView caption;
    @InjectView(R.id.metadata)
    ContentInnerMetadata metadata;

    public ContentInnerText(Context context) {
        this(context, null);
    }

    public ContentInnerText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentInnerText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.card_inner_content_text, this, true);
        ButterKnife.inject(this, this);
    }

    @Override
    public void setWave(Wave wave) {
        caption.setText(wave.getContent().getCaption());
        metadata.setWave(wave);
    }
}
