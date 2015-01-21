package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;

import org.ocpsoft.prettytime.PrettyTime;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 1/20/15.
 */
public class ContentInnerMetadata extends LinearLayout {
    private static PrettyTime prettyTime = new PrettyTime();

    @InjectView(R.id.timestamp)
    TextView timestamp;
    @InjectView(R.id.view_counter)
    TextView viewCounter;

    public ContentInnerMetadata(Context context) {
        this(context, null);
    }

    public ContentInnerMetadata(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentInnerMetadata(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.card_inner_content_metadata, this, true);
        ButterKnife.inject(this, this);
    }

    public void setWave(Wave wave) {
        timestamp.setText(prettyTime.format(wave.getCreatedAt()));
        viewCounter.setText(getViewCountText(wave));
    }

    private String getViewCountText(Wave wave) {
        String viewCount;
        if (wave.getViews() >= 1000) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_EVEN);
            viewCount = df.format(wave.getViews() / 1000f) + "k";
        } else {
            viewCount = wave.getViews() + "";
        }

        return viewCount + " views";
    }
}
