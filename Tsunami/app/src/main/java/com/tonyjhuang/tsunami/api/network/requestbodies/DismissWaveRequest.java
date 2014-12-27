package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;

/*
{
  "guid": "12345",
  "wave_id": 1
}
*/
public class DismissWaveRequest {
    @Expose
    private String guid;
    @Expose
    private long waveId;

    public DismissWaveRequest(String guid, long waveId) {
        this.guid = guid;
        this.waveId = waveId;
    }
}
