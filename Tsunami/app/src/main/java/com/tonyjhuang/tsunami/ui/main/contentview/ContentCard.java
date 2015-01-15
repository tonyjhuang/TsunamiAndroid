package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

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
