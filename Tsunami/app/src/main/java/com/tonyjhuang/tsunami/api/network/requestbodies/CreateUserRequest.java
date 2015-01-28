package com.tonyjhuang.tsunami.api.network.requestbodies;

import com.google.gson.annotations.Expose;

/*
{
  "userId": "f9852ca7-75e0-4e00-8229-125232ba14f8"
}
 */
public class CreateUserRequest {
    @Expose
    final String guid;

    public CreateUserRequest(String guid) {
        this.guid = guid;
    }
}
