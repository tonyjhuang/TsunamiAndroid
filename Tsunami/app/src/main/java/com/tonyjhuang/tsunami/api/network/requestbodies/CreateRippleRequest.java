package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;

/*
{
  "latitude": 50,
  "longitude": 50,
  "wave_id": 1,
  "userId": "f9852ca7-75e0-4e00-8229-125232ba14f8"
}
 */
public class CreateRippleRequest {
    @Expose
    final long waveId;
    @Expose
    final long userId;
    @Expose
    final double latitude;
    @Expose
    final double longitude;

    public CreateRippleRequest(long userId, long waveId, double latitude, double longitude) {
        this.userId = userId;
        this.waveId = waveId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
