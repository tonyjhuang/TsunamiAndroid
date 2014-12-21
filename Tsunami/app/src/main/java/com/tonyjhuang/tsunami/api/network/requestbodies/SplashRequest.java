package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;

/*
{
  "latitude": 123.4567,
  "longitude": 123.4567,
  "title": "wave title",
  "body": "wave content",
  "guid": "f9852ca7-75e0-4e00-8229-125232ba14f8"
}
 */
public class SplashRequest {
    @Expose
    final String guid;
    @Expose
    final String title;
    @Expose
    final String body;
    @Expose
    final double latitude;
    @Expose
    final double longitude;

    public SplashRequest(String guid, String title, String body, double latitude, double longitude) {
        this.guid = guid;
        this.title = title;
        this.body = body;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
