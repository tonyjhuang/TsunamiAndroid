package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.WaveContent;

import org.ocpsoft.prettytime.PrettyTime;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tonyjhuang on 9/7/14.
 */
public class ContentCard extends FrameLayout {
    private static PrettyTime prettyTime = new PrettyTime();

    @InjectView(R.id.alias)
    TextView alias;
    @InjectView(R.id.ripples)
    TextView ripples;
    @InjectView(R.id.comments_text)
    TextView comments;
    @InjectView(R.id.content_container)
    FrameLayout container;

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
        ButterKnife.inject(this, inflater.inflate(R.layout.card_content, this, true));
    }

    public void setWave(Wave wave) {
        this.wave = wave;
        container.removeAllViews();
        if (wave == null) return;

        String numRipples;
        if(wave.getRipples().size() < 1000)
            numRipples = String.valueOf(wave.getRipples().size());
        else
            numRipples = String.valueOf(wave.getRipples().size() / 1000f) + "k";

        alias.setText(wave.getUser().getName());
        comments.setText("See " + wave.getNumComments() + " comments...");
        ripples.setText(numRipples);

        if(wave.getContent().getContentType().equals(WaveContent.ContentType.IMAGE_LINK)) {
            ContentInnerImage innerImage = new ContentInnerImage(getContext());
            innerImage.setWave(wave);
            container.addView(innerImage);
        }
    }


    public Wave getWave() {
        return wave;
    }
}
