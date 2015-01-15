package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;

import org.ocpsoft.prettytime.PrettyTime;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 1/14/15.
 */
public class ContentInnerImage extends LinearLayout {

    private static PrettyTime prettyTime = new PrettyTime();

    @InjectView(R.id.image)
    ImageView imageView;
    @InjectView(R.id.timestamp)
    TextView timestamp;
    @InjectView(R.id.view_counter)
    TextView viewCounter;
    @InjectView(R.id.caption)
    TextView caption;

    public ContentInnerImage(Context context) {
        this(context, null);
    }

    public ContentInnerImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentInnerImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.card_inner_content_image, this, true);
        ButterKnife.inject(this, this);
    }

    public void setWave(Wave wave) {
        if (wave == null) return;

        String viewCount = "";
        if (wave.getViews() >= 1000) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_EVEN);
            viewCount += df.format(wave.getViews() / 1000f) + "k";
        } else {
            viewCount += wave.getViews();
        }

        viewCount += " views";

        Picasso.with(getContext())
                .load(wave.getContent().getBody())
                .fit()
                .centerCrop()
                .into(imageView);

        caption.setText(wave.getContent().getTitle());
        timestamp.setText(prettyTime.format(wave.getCreatedAt()));
        viewCounter.setText(viewCount);
    }
}
