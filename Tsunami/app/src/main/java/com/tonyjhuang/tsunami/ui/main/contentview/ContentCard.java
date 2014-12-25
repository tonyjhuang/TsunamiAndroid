package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
    PrettyTime prettyTime = new PrettyTime();

    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.body)
    TextView body;
    @InjectView(R.id.info)
    TextView info;

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
            title.setText(wave.getContent().getTitle());
            body.setText(wave.getContent().getBody());

            String infoText = "";
            infoText += (wave.getUser() == null ? "Anonymous" : "");
            infoText += " " + getResources().getString(R.string.content_info_text_divider) + " ";
            infoText += prettyTime.format(wave.getCreatedAt());
            this.info.setText(infoText);
        }
    }

    public Wave getWave() {
        return wave;
    }
}
