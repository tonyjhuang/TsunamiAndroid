package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tony on 1/25/15.
 */
public class CommentRequest {
    @Expose
    @SerializedName("guid")
    final String userId;
    @Expose
    final long waveId;
    @Expose
    final String body;

    public CommentRequest(String userId, long waveId, String body) {
        this.userId = userId;
        this.waveId = waveId;
        this.body = body;
    }
}
