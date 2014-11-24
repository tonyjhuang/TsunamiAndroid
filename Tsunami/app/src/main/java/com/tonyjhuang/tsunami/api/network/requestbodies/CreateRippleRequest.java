package com.tonyjhuang.tsunami.api.network.requestbodies;

/*
{
  "latitude": 50,
  "longitude": 50,
  "wave_id": 1,
  "guid": "f9852ca7-75e0-4e00-8229-125232ba14f8"
}
 */
public class CreateRippleRequest {
    final int waveId;
    final String guid;
    final double latitude;
    final double longitude;

    public CreateRippleRequest(String guid, int waveId, double latitude, double longitude) {
        this.waveId = waveId;
        this.guid = guid;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
