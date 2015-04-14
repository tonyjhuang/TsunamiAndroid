package com.tonyjhuang.tsunami.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*"content":
{
    "id": 1,
    "type": "TextContent",
    "caption": "sux"
}
 */
public class WaveContent extends ApiObject {
    @Expose
    private String caption;
    @Expose
    private ContentType type;

    public String getCaption() {
        return caption;
    }

    public ContentType getType() {
        return type;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public static WaveContent createDebugWaveContent(String title, String body) {
        return new WaveContent(title, body);
    }

    public static WaveContent createDebugWaveContent(String title, String body, ContentType contentType) {
        return new WaveContent(title, body, contentType);
    }

    private WaveContent(String title, String caption) {
        this(title, caption, ContentType.text_content);
    }

    private WaveContent(String title, String caption, ContentType type) {
        //this.title = title;
        this.caption = caption;
        this.type = type;
    }

    public enum ContentType {
        @SerializedName("TextContent")
        text_content,
        image_link
    }
}
