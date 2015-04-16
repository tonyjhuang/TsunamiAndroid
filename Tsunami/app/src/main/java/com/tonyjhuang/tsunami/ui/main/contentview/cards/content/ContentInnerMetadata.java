package com.tonyjhuang.tsunami.ui.main.contentview.cards.content;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.utils.Haversine;

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
    private static DecimalFormat df = new DecimalFormat("#.#");

    static {
        df.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    @InjectView(R.id.ripple_counter)
    TextView rippleCounter;
    @InjectView(R.id.timestamp)
    TextView timestamp;
    @InjectView(R.id.view_counter)
    TextView viewCounter;
    @InjectView(R.id.distance)
    TextView distance;

    private LocationInfo locationInfo;

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
        locationInfo = new LocationInfo(context);
    }

    public void setWave(Wave wave) {
        Ripple splash = wave.getSplash();
        if (splash == null) return;

        Timber.d(wave.toString());

        rippleCounter.setText(getRippleCountText(wave.getRipples().size()));
        if(wave.getCreatedAt() != null) {
            timestamp.setText(prettyTime.format(wave.getCreatedAt()));
        }
        viewCounter.setText(getViewCountText(wave.getViews()));
        distance.setText(getDistanceText((float) splash.getLatitude(), (float) splash.getLongitude()));
    }

    private Spanned getRippleCountText(int ripples) {
        String ripplesText = wrtThousand(ripples);
        String rippleCountText = ripplesText +
                " " + getResources().getQuantityString(R.plurals.content_metadata_ripples, ripples);
        int color = getResources().getColor(R.color.primary_dark);

        SpannableStringBuilder builder = new SpannableStringBuilder(rippleCountText);
        setSpan(builder, new ForegroundColorSpan(color), rippleCountText);
        setSpan(builder, new StyleSpan(Typeface.BOLD), ripplesText);

        return builder;
    }

    private void setSpan(SpannableStringBuilder builder, CharacterStyle style, String target) {
        String original = builder.toString();
        int start = original.indexOf(target);
        builder.setSpan(style, start, start + target.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private String getViewCountText(int views) {
        return wrtThousand(views) +
                " " + getResources().getQuantityString(R.plurals.content_metadata_views, views);
    }

    private String getDistanceText(float lat, float lng) {
        float dist = Haversine.haversineInMiles(locationInfo.lastLat, locationInfo.lastLong, lat, lng);
        if (dist < 0.1f)
            return "next door";
        else
            return df.format(dist) + " miles away";
    }

    private String wrtThousand(int num) {
        if (num >= 1000) {
            return String.valueOf(df.format(num / 1000f)) + "k";
        } else {
            return String.valueOf(num);
        }
    }
}
