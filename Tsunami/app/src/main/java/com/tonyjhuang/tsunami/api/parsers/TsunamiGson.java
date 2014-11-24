package com.tonyjhuang.tsunami.api.parsers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Creates the Gson adapter that we want to user to parse our network JSON
 * objects into Tsunami ApiObjects
 */
public class TsunamiGson {
    public static Gson buildGson() {
        return new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }
}
