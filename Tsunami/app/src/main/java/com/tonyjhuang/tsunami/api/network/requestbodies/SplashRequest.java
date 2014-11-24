package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;

/*
{
  "latitude": 123.4567,
  "longitude": 123.4567,
  "content": "wave content",
  "guid": "f9852ca7-75e0-4e00-8229-125232ba14f8"
}
 */
public class SplashRequest {
    @Expose
    final String guid;
    @Expose
    final String content;
    @Expose
    final double latitude;
    @Expose
    final double longitude;

    public SplashRequest(String guid, String content, double latitude, double longitude) {
        this.guid = guid;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
