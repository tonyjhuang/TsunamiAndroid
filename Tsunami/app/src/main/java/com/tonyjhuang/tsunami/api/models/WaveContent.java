package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;

/*
{
    "body": null,
    "id": 111,
    "title": null,
    "wave_id": 141
}
 */
public class WaveContent {
    @Expose
    private long id;
    @Expose
    private long waveId;
    @Expose
    private String title;
    @Expose
    private String body;

    public long getId() {
        return id;
    }

    public long getWaveId() {
        return waveId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public static WaveContent createDebugWaveContent(String title, String body) {
        return new WaveContent(title, body);
    }

    private WaveContent(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
