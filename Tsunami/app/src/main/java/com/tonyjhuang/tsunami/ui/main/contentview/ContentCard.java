package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;

import org.ocpsoft.prettytime.PrettyTime;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tonyjhuang on 9/7/14.
 */
public class ContentCard extends FrameLayout {
    private static PrettyTime prettyTime = new PrettyTime();

    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.info)
    TextView info;
    @InjectView(R.id.divider)
    View divider;
    @InjectView(R.id.body)
    TextView body;


    /**
     * The wave that we should be displaying currently.
     */
    private Wave wave;

    public ContentCard(Context context) {
        super(context);
        setup(context);
    }

    public ContentCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public ContentCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    public void setup(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.card_content, this, true);
        ButterKnife.inject(this, this);
    }

    public void setWave(Wave wave) {
        this.wave = wave;
        if (wave != null) {
            String titleText = wave.getContent().getTitle();
            String bodyText = wave.getContent().getBody();

            hideBody(TextUtils.isEmpty(bodyText) || TextUtils.isEmpty(titleText));

            title.setText(TextUtils.isEmpty(titleText) ? bodyText : titleText);
            body.setText(wave.getContent().getBody());

            String infoText = wave.getUser().getName();
            if (wave.getCreatedAt() != null) {
                infoText += " " + getResources().getString(R.string.content_info_text_divider) + " ";
                infoText += ContentCard.prettyTime.format(wave.getCreatedAt());
            }
            this.info.setText(infoText);
        }
    }

    private void hideBody(boolean hide) {
        int visibility = hide ? GONE : VISIBLE;
        divider.setVisibility(visibility);
        body.setVisibility(visibility);
    }

    public Wave getWave() {
        return wave;
    }
}
