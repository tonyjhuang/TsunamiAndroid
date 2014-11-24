package com.tonyjhuang.tsunami.api.network.requestbodies;

/*
{
  "latitude": 123.4567,
  "longitude": 123.4567,
  "content": "wave content",
  "guid": "f9852ca7-75e0-4e00-8229-125232ba14f8"
}
 */
public class SplashRequest {
    final double latitude;
    final double longitude;
    final String content;
    final String guid;

    public SplashRequest(String guid, String content, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.content = content;
        this.guid = guid;
    }
}
