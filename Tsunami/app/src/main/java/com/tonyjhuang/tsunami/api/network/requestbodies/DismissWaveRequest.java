package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;

/*
{
  "userId": "12345",
  "wave_id": 1
}
*/
public class DismissWaveRequest {
    @Expose
    final long userId;
    @Expose
    final long waveId;

    public DismissWaveRequest(long userId, long waveId) {
        this.userId = userId;
        this.waveId = waveId;
    }
}
