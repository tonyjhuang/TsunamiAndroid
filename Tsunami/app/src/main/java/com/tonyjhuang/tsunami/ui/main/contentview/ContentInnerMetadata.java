package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.Wave;
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

        timestamp.setText(prettyTime.format(wave.getCreatedAt()));
        viewCounter.setText(getViewCountText(wave.getViews()));
        if(splash == null) return;
        distance.setText(getDistanceText((float) splash.getLatitude(), (float) splash.getLongitude()));
    }

    private String getViewCountText(int views) {
        String viewCount;
        if (views >= 1000) {
            viewCount = df.format(views / 1000f) + "k";
        } else {
            viewCount = views + "";
        }

        return viewCount + " views";
    }

    private String getDistanceText(float lat, float lng) {
        float dist = Haversine.haversineInMiles(locationInfo.lastLat, locationInfo.lastLong, lat, lng);
        return df.format(dist) + " miles away";

    }
}
