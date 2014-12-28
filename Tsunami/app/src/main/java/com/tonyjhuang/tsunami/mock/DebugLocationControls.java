package com.tonyjhuang.tsunami.mock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tony on 12/27/14.
 */
public class DebugLocationControls extends LinearLayout {
    private static final double INTERVAL = 0.0025;

    @InjectView(R.id.latitude)
    TextView latitude;
    @InjectView(R.id.longitude)
    TextView longitude;

    private LocationInfo locationInfo;
    private float lastMockLat;
    private float lastMockLong;

    public DebugLocationControls(Context context) {
        this(context, null);
    }

    public DebugLocationControls(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DebugLocationControls(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ButterKnife.inject(this, inflate(context, R.layout.view_debug_location_controls, this));
    }


    @OnClick(R.id.left)
    public void onLeftClick(View view) {
        setMockLocation(0, -INTERVAL);
    }

    @OnClick(R.id.right)
    public void onRightClick(View view) {
        setMockLocation(0, INTERVAL);
    }

    @OnClick(R.id.up)
    public void onUpClick(View view) {
        setMockLocation(INTERVAL, 0);
    }

    @OnClick(R.id.down)
    public void onDownClick(View view) {
        setMockLocation(-INTERVAL, 0);
    }

    public void setCurrentLocation(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
        lastMockLat = locationInfo.lastLat;
        lastMockLong = locationInfo.lastLong;
        latitude.setText(locationInfo.lastLat + "");
        longitude.setText(locationInfo.lastLong + "");
    }

    public static interface LocationListener {
        public void onLocationChanged(LocationInfo locationInfo);
    }

    private LocationListener locationListener;

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    private void setMockLocation(double dLat, double dLng) {
        locationInfo.lastLat = (float) dLat + lastMockLat;
        locationInfo.lastLong = (float) dLng + lastMockLong;
        if (locationListener != null)
            locationListener.onLocationChanged(locationInfo);
        setCurrentLocation(locationInfo);
    }
}
