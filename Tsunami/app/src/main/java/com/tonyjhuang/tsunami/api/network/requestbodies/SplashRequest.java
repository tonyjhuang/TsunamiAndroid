package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;
import com.tonyjhuang.tsunami.api.models.WaveContent;

/*
{
  "latitude": 123.4567,
  "longitude": 123.4567,
  "title": "wave title",
  "body": "wave content",
  "userId": "f9852ca7-75e0-4e00-8229-125232ba14f8"
}
 */
public class SplashRequest {
    @Expose
    final long userId;
    @Expose
    final String title;
    @Expose
    final String body;
    @Expose
    final double latitude;
    @Expose
    final double longitude;
    @Expose
    final WaveContent.ContentType contentType;

    public SplashRequest(long userId,
                         String title,
                         String body,
                         WaveContent.ContentType contentType,
                         double latitude,
                         double longitude) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contentType = contentType;
    }
}
