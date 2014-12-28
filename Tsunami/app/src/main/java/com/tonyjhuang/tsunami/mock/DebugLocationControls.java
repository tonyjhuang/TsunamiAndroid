package com.tonyjhuang.tsunami.mock;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.tonyjhuang.tsunami.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tony on 12/27/14.
 */
public class DebugLocationControls extends LinearLayout implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String PROVIDER = "flp";
    private static final float ACCURACY = 3.0f;

    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private GoogleApiClient apiClient;
    private boolean connected = false;

    @InjectView(R.id.latitude)
    TextView latitude;
    @InjectView(R.id.longitude)
    TextView longitude;

    public DebugLocationControls(Context context) {
        this(context, null);
    }

    public DebugLocationControls(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DebugLocationControls(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ButterKnife.inject(this, inflate(context, R.layout.view_debug_location_controls, this));

        apiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void connect() {
        locationProviderApi.setMockMode(apiClient, true);
    }

    public void disconnect() {
        locationProviderApi.setMockMode(apiClient, false);
    }


    @OnClick(R.id.left)
    public void onLeftClick(View view) {
        connect();
        locationProviderApi.setMockLocation(apiClient, createLocation(0, 0));
    }

    public void setCurrentLocation(double lat, double lng) {
        latitude.setText(lat + "");
        longitude.setText(lng + "");
    }

    public Location createLocation(double lat, double lng) {
        // Create a new Location
        Location newLocation = new Location(PROVIDER);
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);
        newLocation.setAccuracy(ACCURACY);
        return newLocation;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
