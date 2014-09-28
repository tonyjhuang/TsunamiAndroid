package com.tonyjhuang.tsunami.api.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Helper class to build out our TsunamiService network interface.
 * Created by tonyjhuang on 9/28/14.
 */
public class TsunamiServiceBuilder {
    public static final String ENDPOINT = "http://104.131.27.176:8080";

    private static RequestInterceptor headerInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", "Android");
        }
    };

    public static TsunamiService build() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(headerInterceptor)
                .setConverter(new GsonConverter(buildGson()))
                .build();

        return restAdapter.create(TsunamiService.class);
    }

    /**
     * Creates the Gson adapter that we want to user to parse our network JSON
     * objects into Tsunami POJOs
     */
    public static Gson buildGson() {
        return new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

}
