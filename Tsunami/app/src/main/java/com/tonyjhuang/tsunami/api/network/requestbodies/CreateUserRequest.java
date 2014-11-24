package com.tonyjhuang.tsunami.api.network.requestbodies;

/*
{
  "guid": "f9852ca7-75e0-4e00-8229-125232ba14f8"
}
 */
public class CreateUserRequest {
    final String guid;

    public CreateUserRequest(String guid) {
        this.guid = guid;
    }
}
