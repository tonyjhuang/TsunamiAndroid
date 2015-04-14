package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;
import com.tonyjhuang.tsunami.api.models.WaveContent;

/*
{
  "latitude": 123.4567,
  "longitude": 123.4567,
  "caption": "wave content",
  "userId": "f9852ca7-75e0-4e00-8229-125232ba14f8",
  "type": "TextContent"
}
 */
public class SplashRequest {
    @Expose
    final long userId;
    @Expose
    final String caption;
    @Expose
    final WaveContent.ContentType type;
    @Expose
    final double latitude;
    @Expose
    final double longitude;

    public SplashRequest(long userId,
                         String caption,
                         WaveContent.ContentType type,
                         double latitude,
                         double longitude) {
        this.userId = userId;
        this.type = type;
        this.caption = caption;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
