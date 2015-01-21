package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    @Expose
    @SerializedName("type")
    private ContentType contentType;


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

    public ContentType getContentType() {
        return contentType;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public static WaveContent createDebugWaveContent(String title, String body) {
        return new WaveContent(title, body);
    }

    public static WaveContent createDebugWaveContent(String title, String body, ContentType contentType) {
        return new WaveContent(title, body, contentType);
    }

    private WaveContent(String title, String body) {
        this(title, body, ContentType.TEXT);
    }

    private WaveContent(String title, String body, ContentType contentType) {
        this.title = title;
        this.body = body;
        this.contentType = contentType;
    }

    public static enum ContentType {
        @SerializedName("text")
        TEXT,
        @SerializedName("image_link")
        IMAGE_LINK
    }
}
