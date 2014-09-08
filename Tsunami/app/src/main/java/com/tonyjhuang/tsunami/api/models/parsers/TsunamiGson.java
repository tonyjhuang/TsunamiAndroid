package com.tonyjhuang.tsunami.api.models.parsers;

import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.tonyjhuang.tsunami.api.models.Wave.WaveContentType;

/**
 * Singleton Gson wrapper
 * Created by tonyjhuang on 8/17/14.
 */
@Singleton
public class TsunamiGson {
    private final Gson gson;

    @Inject
    public TsunamiGson() {
        gson = buildGson();
    }

    public static Gson buildGson() {
        return new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(WaveContentType.class, EnumGsonAdapter.getInstance(WaveContentType.class))
                .registerTypeAdapter(Location.class, new LocationGsonAdapter())
                .create();
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
